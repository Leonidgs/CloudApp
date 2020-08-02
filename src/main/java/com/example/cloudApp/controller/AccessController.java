package com.example.cloudApp.controller;

import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessController {
	
/*

	@GetMapping("/homee")
	public String home() {
		return "Success";
	}
/*
/*	
	@Secured("ROLE_ADMIN")
	@GetMapping("/admin")
	public String admin(Authentication authentication) {
		return "Success [" + authentication.getName() + "-" +
				authentication.getAuthorities()
				.stream()
				.map(role -> role.getAuthority())
				.collect(Collectors.joining(",")) 
				+ "]";
	}
*/	
}
