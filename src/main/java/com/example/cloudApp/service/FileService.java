package com.example.cloudApp.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.example.cloudApp.dto.DownloadFileDto;
import com.example.cloudApp.dto.FileDto;
import com.example.cloudApp.dto.ResponseDto;

public interface FileService {
	
	List<FileDto> getFiles(Integer folderId);
	
	Boolean createHashLink(Integer id) throws NoSuchAlgorithmException;
		
	String getHashLink(Integer id);
	
	ResponseDto saveFile(MultipartFile file, Integer folderId);
	
	DownloadFileDto getFileParams(Integer fileId);

}

