package com.example.cloudApp.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Value("${cloud.folder.root}")
	private String cloudFolder;
	
	
	@GetMapping("/upload")
	public String uploadPage(Model model) {
		return "upload";
	}
	
	@PostMapping("/upload")
	public String sendFile(@RequestParam(name="uploadFile")MultipartFile file,Model model) throws IOException {
		var extension = FilenameUtils.getExtension(file.getOriginalFilename());
		
		var directory = new File(cloudFolder);
		
		var transferFile = File.createTempFile("inordic_", "_temp." + extension, directory);
		file.transferTo(transferFile);
		
		logger.debug("File with size: " + transferFile.length() / 1024.f);
		logger.info("File with size: " + FileUtils.byteCountToDisplaySize(transferFile.length()));
		
//		logger.error(msg);
//		logger.warn(msg);
//		logger.info(msg);
//		logger.debug(msg);
//		logger.trace(msg);
		
		
		// Reader/Writer (с символами)
		// InputStream/OutputStream (с байтам)
		// FileInputStream/FileOutputStream
		// ZipInputStream/ZipOutputStream
		
		try (var fileOut = new FileOutputStream(new File(directory,"test.zip"));
			 var zipFileOut = new ZipOutputStream(fileOut)) {
			
			zipFileOut.putNextEntry(new ZipEntry(file.getOriginalFilename()));
			zipFileOut.write(Files.readAllBytes(Paths.get(transferFile.getPath())));
			zipFileOut.closeEntry();
		}
		
		
//		try () {
//			
//		}
//		catch {
//			
//		}
//		finally {
//			
//		}
		
		
		return "redirect:/home";
	}
}
