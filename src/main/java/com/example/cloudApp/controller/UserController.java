package com.example.cloudApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloudApp.dto.UserTariffInfo;
import com.example.cloudApp.model.User;
import com.example.cloudApp.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	//@Qualifier("userServiceImpl")
	private UserService userDB;

	@PostMapping("/users")
	private List<User> getAllUsers() {
		
		return userDB.getAllUsers();
	}
	
	@GetMapping("/user/{id}")
	private User getUser(@PathVariable(name="id")Integer id) {
		return userDB.getUserById(id);
	}
	
	@PostMapping("/user")
	private boolean addUser(@RequestBody User user) {
		
		if(userDB.checkUser(user.getLogin(), user.getEmail())) {

			return userDB.addUser(user);
		}

		return userDB.checkUser(user.getLogin(), user.getEmail());
	}
	
	@PostMapping("/user/edit")
	private boolean updateUser(@RequestBody User user) {
		
		return userDB.editUser(user);

	}
	
	@DeleteMapping("/user")
	private boolean deleteUser(@RequestParam(name="id")Integer id) {

		return userDB.deleteUserById(id);
	}
	
	@PostMapping("/user/info/{id}")
	private UserTariffInfo getInfo(@PathVariable(name="id")Integer id) {
		return userDB.getUserInfo(id);
	}
}
