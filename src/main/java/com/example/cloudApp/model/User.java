package com.example.cloudApp.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "password")
public class User {
	
	private Integer id;
	private String name;
	private String login;
	private String email;
	private String tariff;
	private Integer tariff_id;
	private String role;
	private Integer role_id;
	private String password;

}
