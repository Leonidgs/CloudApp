package com.example.cloudApp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;


import javax.servlet.http.HttpSession;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloudApp.dto.CreateFolderDto;
import com.example.cloudApp.dto.FolderDto;
import com.example.cloudApp.dto.ResponseDto;
import com.example.cloudApp.dto.UploadFileDto;
import com.example.cloudApp.service.FileService;
import com.example.cloudApp.service.FolderService;

@RestController
public class FileController {

	@Autowired
	private HttpSession httpSession;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private FolderService folderService;

	
	@Value("${cloud.folder.root}")
	private String cloudFolder;


	@PostMapping("/download")
	private ResponseEntity<byte[]> download(@RequestParam(name="fileId",required=false)Integer fileId) throws IOException {

		var dto = fileService.getFileParams(fileId);
		var path = Paths.get(dto.getPath());
		
		var tika = new Tika();
		var mimeType = tika.detect(path);
		
		var content = Files.readAllBytes(path);
		
		System.out.println(dto.getName());
		var httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, dto.getName());
		httpHeaders.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
		httpHeaders.add(HttpHeaders.PRAGMA, "no-cache");
		httpHeaders.add(HttpHeaders.EXPIRES, "0");
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(mimeType) + "; charset=utf-8");
	
		return ResponseEntity.ok()
				.headers(httpHeaders)
				.contentLength(content.length)
				//.contentType(MediaType.parseMediaType(mimeType))
				.body(content);
	
	}


	@PostMapping("/getfiles")
	private FolderDto getFolderFiles(@RequestParam(name="folderId",required=false)Integer folderId) throws IOException {
		
		FolderDto folder;
		
		System.out.println(folderId);
		if(folderId!=null) {
			
			folder = new FolderDto(folderId,folderService.getParentFolderId(folderId),fileService.getFiles(folderId));
			return folder;
			
		}else {
			
			var userId = (Integer)httpSession.getAttribute("id");
			
			folderId = folderService.getRootFolderId(userId);
			
			folder = new FolderDto(folderId,null,fileService.getFiles(folderId));

			return folder;
		}
		
	}

	@GetMapping("/share/{id}")
	private boolean share(@PathVariable(name="id")Integer id) throws NoSuchAlgorithmException{
		
		return fileService.createHashLink(id);
				 	
	}

	@PostMapping("/upload/file")
	private boolean uploadFile(@ModelAttribute UploadFileDto uploadFileDto){

		fileService.saveFile(uploadFileDto.getFile(), uploadFileDto.getFolderId());

		return true;
				 	
	}


	@PostMapping("/upload/folder")
	public ResponseDto createFolder(@RequestBody CreateFolderDto createFolderDto) {
		
		
		return folderService.createFolder(createFolderDto);
		
		/*
		if(folderDto.getParentFolderId()!=null) {
			
		}else {
			
			var folderid=folderService.getRootFolderId(folderDto);
			folderDto.setParentFolderId(folderid);
			
			return folderService.createFolder(folderDto);
		}
		*/
	}

}
