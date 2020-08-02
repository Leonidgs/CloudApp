package com.example.cloudApp.controller;



import java.util.ArrayList;


import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {
	
	@Autowired
	private HttpSession httpSession;
	
	@GetMapping("/home")
	public String homePage(Model model) {
		
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			model.addAttribute("navitems", new ArrayList<String>(Arrays.asList("Создать папку", "Загрузить файл")));
			System.out.println("Не зарегистрирован");
		}
		else {
			//System.out.println("Зарегистрирован");
			//System.out.println(httpSession.getAttribute("id"));
			model.addAttribute("userid",httpSession.getAttribute("id"));
		}

		return "home";
	}
}
