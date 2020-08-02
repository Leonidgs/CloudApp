package com.example.cloudApp.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.cloudApp.dto.CreateFolderDto;
import com.example.cloudApp.dto.ResponseDto;
import com.example.cloudApp.model.User;
import com.example.cloudApp.service.FolderService;
import com.example.cloudApp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FolderService folderService;

	@GetMapping("/login")
	public String loginPage(Model model, @RequestParam(name="error",required=false)Boolean error) {
		model.addAttribute("form", new User());
		if (error != null && error) {
			model.addAttribute("error", error);
		}
		
		return "login";
	}
	
	@GetMapping("/registration")
	public String registrationPage(Model model, @RequestParam(name="error",required=false)String error) {
		model.addAttribute("newUser", new User());
		if (error!=null) {
			model.addAttribute("error", error);
		}
		return "registration";
	}
	
	@PostMapping("/registration")
	public String registerUser(User newUser,HttpServletRequest  httpServletRequest ) throws ServletException  {
		
		if(userService.checkUserLogin(newUser.getLogin())) {
			if (userService.checkUser(newUser.getLogin(), newUser.getEmail())) {	

				userService.addUser(newUser);
				User user = userService.getUserByLogin(newUser.getLogin());
				ResponseDto response = folderService.createFolder(new CreateFolderDto(null, null, user.getId()));

			}else {
				return "redirect:registration?error=user_exist";
			}			
			httpServletRequest.login(newUser.getLogin(), newUser.getPassword());
			return "redirect:home";
		}else {
			return "redirect:registration?error=login_exist";
		}

	}
 }
