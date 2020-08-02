package com.example.cloudApp.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cloudApp.dto.DownloadFileDto;
import com.example.cloudApp.dto.FileDto;
import com.example.cloudApp.dto.ResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service
//@ для создания log - библиотека
@Slf4j
public class FileServiceImpl implements FileService {
	
	@Value("${cloud.folder.root}")
	private String cloudFolder;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<FileDto> getFiles(Integer folderId) {
		
		List<FileDto> files = jdbcTemplate.query("SELECT id,name,size,type, date FROM("
				+ " SELECT id,name, (SELECT SUM(size) FROM cloud_storage.file WHERE folder_id = ?) AS size,NULL AS type,TO_CHAR(NOW(),'dd-mm-yyyy hh24:mi') as date FROM cloud_storage.folder "
				+ " WHERE parent_id = ? ORDER BY name ASC) t1 "
				+ " UNION ALL"
				+ " SELECT id,name,size,type,date FROM ("
				+ " SELECT id,name,size,type,TO_CHAR(created_at,'dd-mm-yyyy hh24:mi') AS date FROM cloud_storage.file WHERE folder_id = ?"
				+ " ORDER BY name ASC) t2", new Object[] {folderId,folderId,folderId}, new BeanPropertyRowMapper<FileDto>(FileDto.class));
			
		return files;

	}
/*	
	@Override
	public List<FileDto> getRootFiles(Integer userId) {
		
		
		List<FileDto> files = jdbcTemplate.query("SELECT id,name,size,type,date FROM("
				+ " SELECT id,name, (SELECT SUM(size) FROM cloud_storage.file WHERE folder_id = (SELECT folder_id FROM cloud_storage.folder WHERE parent_id IS null AND user_id = ?)) AS size,NULL AS type,NOW() AS date FROM cloud_storage.folder "
				+ " WHERE parent_id IS NOT NULL ORDER BY name ASC) t1 "
				+ " UNION ALL"
				+ " SELECT id,name,size,type,date FROM ("
				+ " SELECT id,name,size,type,created_at AS date FROM cloud_storage.file WHERE folder_id = (SELECT folder_id FROM cloud_storage.folder WHERE parent_id IS null AND user_id = ?)"
				+ " ORDER BY name ASC) t2", new Object[] {userId,userId}, new BeanPropertyRowMapper<FileDto>(FileDto.class));
		
		//return files.stream().filter(file -> file.getId()==null).collect(Collectors.toList());
		return files;
	}
*/	
	@Override
	public Boolean createHashLink(Integer id) throws NoSuchAlgorithmException {
		
		var md = MessageDigest.getInstance("SHA-256");
		var msg = id.toString();
		md.update(msg.getBytes());	
		byte[] digest = md.digest();
		var hexString = new StringBuffer();
		
		 for (int i = 0;i<digest.length;i++) {
	         hexString.append(Integer.toHexString(0xFF & digest[i]));
	      }


		//System.out.println(hexString.toString());


		int count = jdbcTemplate.update("INSERT INTO cloud_storage.\"link\" (\n" +
				"	hlink, file_id)\n" + 
				"	VALUES (?, ?)", new Object[] {hexString.toString(), id});
		 
		
		return count > 0;
	
	}

	@Override
	public String getHashLink(Integer id) {
		
		return null;
	}

	@Override
	public ResponseDto saveFile(MultipartFile file, Integer folderId) {
		// создаём путь
		
		System.out.println(folderId);
		
		Object[] params = {folderId};
		String path = jdbcTemplate.queryForObject("SELECT path from cloud_storage.folder where id = ?", params, String.class);
		String directory = cloudFolder + path + File.separator + file.getOriginalFilename(); //путь для файла
		
		var response = new ResponseDto();
		
		// проверяем нет ли уже такого файла у этого user. Если есть - ошибка в запросе.
		if (Files.exists(Paths.get(directory))) {
			response.setError("Такой файл уже существует в данной папке. Измените имя файла.");	
		} else if(file.isEmpty()) {
			 response.setError("Вам не удалось загрузить файл потому что файл пустой.");
		} else {
			try {
				// создаём директорию
				Path createdPath = Files.createFile(Paths.get(directory));
				
				// сохраняем в БД наш файл и возвращаем его сгенерённый id
				
				String[] forType = file.getOriginalFilename().split("\\.");
				
				jdbcTemplate.update("INSERT INTO cloud_storage.file" + 
						"	(name, folder_id, avail, size, type)" + 
						"	VALUES (?, ?, ?, ?, ?)", new Object[] { forType[0], folderId, true, file.getSize() / 1024,forType[forType.length-1]});

	/*			
				SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
						.withSchemaName("cloud_storage")
						.withTableName("file")
						.usingGeneratedKeyColumns("id");
				
				Map<String, Object> par = new HashMap<>();
				par.put("folder_id", folderId);
				par.put("name", file.getOriginalFilename());
				par.put("avail", true);
				// размер в кб
				par.put("size", file.getSize() / 1024);
				String[] forType = file.getOriginalFilename().split(".");
				//par.put("type", forType[forType.length - 1]);
				par.put("type", "txt");
				Integer id = (Integer) insert.executeAndReturnKey(par);
				
				response.setId(id);
*/				
				// записываем файл
				byte[] bytes = file.getBytes();
				Files.write(createdPath, bytes);
		        
			} catch (IOException e) {
				// logger - пишет текст и в консоль и в файл
				// генерится статическая переменная под именем log 
				log.error("Невозможно сохранить файл", e);
				response.setError("Невозможно сохранить файл");
			}	
		}	
		return response;
	}
	
	@Override
	public DownloadFileDto getFileParams(Integer fileId) {
		var params = jdbcTemplate.queryForObject("SELECT f1.name || '.' || f1.type as name,path || '\\' || f1.name || '.' || f1.type as path "
				+ "FROM cloud_storage.folder f "
				+ "LEFT JOIN cloud_storage.file f1 ON f.id = f1.folder_id\r\n" + 
				"  WHERE f1.id = ?", new Object[] {fileId},new BeanPropertyRowMapper<DownloadFileDto>(DownloadFileDto.class));
		
		params.setPath(cloudFolder + File.separator+params.getPath());
		return params;
	}


}


