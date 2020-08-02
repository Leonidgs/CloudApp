package com.example.cloudApp.controller;

import java.nio.file.NoSuchFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.cloudApp.dto.ResponseErrorDto;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {
	
	private static Logger log = LoggerFactory.getLogger(ErrorController.class);
	
	@ExceptionHandler(NoSuchFileException.class)
	public ResponseEntity<ResponseErrorDto> fileNotFound(NoSuchFileException ex) {
		log.error(ex.getMessage());
		return ResponseEntity.badRequest().body(new ResponseErrorDto("Что-то пошло не так. Попробуйте позднее...", 101));
	}

}
