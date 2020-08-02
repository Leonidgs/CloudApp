package com.example.cloudApp.service;

import com.example.cloudApp.dto.CreateFolderDto;
import com.example.cloudApp.dto.ResponseDto;

public interface FolderService {
	
	ResponseDto createFolder(CreateFolderDto createFolderDto);
	
	Integer getRootFolderId(Integer userId);
	
	Integer getParentFolderId(Integer folderId);
}
