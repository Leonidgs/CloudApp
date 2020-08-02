package com.example.cloudApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import com.example.cloudApp.dto.CreateFolderDto;
import com.example.cloudApp.dto.FolderDto;
import com.example.cloudApp.dto.ResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service
//@ для создания log - библиотека
@Slf4j
public class FolderServiceImpl implements FolderService {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@Value("${cloud.folder.root}")
	private String cloudFolder;

	@Override
	public ResponseDto createFolder(CreateFolderDto createFolderDto) {
		
		var userId = createFolderDto.getUserId();
				
		String directory = File.separator + userId;
		
		if (createFolderDto.getParentFolderId() != null) {
			Object[] params = { createFolderDto.getParentFolderId() };
			
			String path = jdbcTemplate.queryForObject("SELECT path from cloud_storage.folder where id = ?", params,
					String.class);
			directory = path + File.separator + createFolderDto.getFolderName(); // путь для новой папки
		}

		var response = new ResponseDto();

		// проверяем нет ли уже корневой папки у этого user. Если есть - ошибка в
		// запросе.
		var fullDirectory = cloudFolder + directory;
		if (Files.exists(Paths.get(fullDirectory))) {
			response.setError("Такая папка уже существует. Ошибка запроса.");
		} else {
			try {
				// создаём директорию
				Files.createDirectory(Paths.get(fullDirectory));

				// сохраняем в БД нашу папку и возвращаем её сгенерённый id
				SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
						.withSchemaName("cloud_storage")
						.withTableName("folder")
						.usingGeneratedKeyColumns("id");

				Map<String, Object> params = new HashMap<>();
				params.put("user_id", userId);
				params.put("path", directory);
				params.put("avail", true);
				params.put("parent_id", createFolderDto.getParentFolderId());
				params.put("name", createFolderDto.getFolderName());
				Integer id = (Integer) insert.executeAndReturnKey(params);

				response.setId(id);

			} catch (IOException e) {
				// logger - пишет текст и в консоль и в файл
				// генерится статическая переменная под именем log
				log.error("Невозможно создать директорию", e);
				response.setError("Невозможно создать директорию");
			}
		}
		return response;
	}
	
	@Override
	public Integer getRootFolderId(Integer userId) {
		
		var folderId = jdbcTemplate.queryForObject("SELECT id FROM cloud_storage.folder WHERE parent_id IS NULL AND user_id = ?"
				, new Object[] {userId},Integer.class);
			
		return folderId;
		
	}

	@Override
	public Integer getParentFolderId(Integer folderId) {
		
		var parentFolderId = jdbcTemplate.queryForObject("SELECT parent_id FROM cloud_storage.folder WHERE id = ?"
				, new Object[] {folderId},Integer.class);
			
		return parentFolderId;
	}

	
}
