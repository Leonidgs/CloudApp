package com.example.cloudApp.service;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		

		var user = userService.getUserByLogin(username);
		
		System.out.println(user.getRole());
		Set<GrantedAuthority> roles = new HashSet<>();
		
		roles.add(new SimpleGrantedAuthority(user.getRole()));
		
		httpSession.setAttribute("id", user.getId());
		
		UserDetails userDetails = new User(user.getLogin(), user.getPassword(), roles);
		
		return userDetails;
	}

}
