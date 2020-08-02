package com.example.cloudApp.service;

import java.util.List;

import com.example.cloudApp.dto.UserTariffInfo;
import com.example.cloudApp.model.User;

public interface UserService {
	
	List<User> getAllUsers();
	
	User getUserById(Integer id);
	
	User getUserByLogin(String login);
	
	boolean addUser(User user);
	
	boolean deleteUserById(Integer id);
	
	boolean checkUser(String login, String email);
	
	boolean editUser(User user);
	
	UserTariffInfo getUserInfo(Integer id);
	
	boolean checkUserLogin(String login);
	

}
