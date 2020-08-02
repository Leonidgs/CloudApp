package com.example.cloudApp.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.cloudApp.dto.OfferDto;
import com.example.cloudApp.dto.UserTariffInfo;
import com.example.cloudApp.model.User;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Integer DEFAULT_TARIFF_ID = 1;
	private static final Integer DEFAULT_ROLE_ID = 1;	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private PasswordEncoder bcryptPasswordEncoder;

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {		
//		List<UserDB> users = jdbcTemplate.query("SELECT * FROM \"user\"", new RowMapper<UserDB>() {
//
//			@Override
//			public UserDB mapRow(ResultSet rs, int rowNum) throws SQLException {
//				UserDB user = new UserDB();
//				
//				user.setUserId(rs.getInt("user_id"));
//				user.setUserName(rs.getString("user_name"));
//				user.setUserMail(rs.getString("user_mail"));
//				user.setUserAge(rs.getInt("user_age"));
//				
//				return user;
//			}
//			
//		});
		
		List<User> users = jdbcTemplate.query("SELECT u.id,u.name,u.login,r.name as role,u.email,t.name as tariff FROM cloud_storage.user as u "
				+ "LEFT JOIN cloud_storage.tariff as t ON t.id = u.tariff_id "
				+ "LEFT JOIN cloud_storage.role as r ON r.id = u.role_id", new BeanPropertyRowMapper(User.class));
		
		return users;
	}
	
	@Override
	public boolean checkUserLogin(String login) {
		int count = jdbcTemplate.queryForObject("SELECT count(id) FROM cloud_storage.user" + 
				"	WHERE login = ?", new Object[] {login}, Integer.class);
		return count == 0;

	}
	
	@Override
	public boolean checkUser(String login, String email) {
		int count = jdbcTemplate.queryForObject("SELECT count(id) FROM cloud_storage.user" + 
				"	WHERE login = ? OR email = ?", new Object[] {login,email}, Integer.class);
		return count == 0;

	}
	

	@Override
	public boolean addUser(User user) {
		Integer tariffId = (user.getTariff_id() != null) ? user.getTariff_id() : DEFAULT_TARIFF_ID;
		Integer roleId = (user.getRole_id() != null) ? user.getRole_id() : DEFAULT_ROLE_ID; 
		int count = jdbcTemplate.update("INSERT INTO cloud_storage.user" + 
				"	(name, login, email, tariff_id, role_id, password)" + 
				"	VALUES (?, ?, ?, ?, ?, ?)", new Object[] { 
						user.getName(), user.getLogin(), user.getEmail(), tariffId, roleId,
						bcryptPasswordEncoder.encode(user.getPassword())});
		return count > 0;
	}

	@Override
	public boolean deleteUserById(Integer id) {
		
		int count = jdbcTemplate.update("DELETE FROM cloud_storage.user " + 
				"WHERE id = ?", new Object[] {id});
		return count > 0;
	}

	@Override
	public User getUserById(Integer id) {
		User user = (User)jdbcTemplate.queryForObject("SELECT u.id,u.name,u.login,u.email,u.role_id,r.name as role,u.tariff_id,t.name as tariff FROM cloud_storage.user u\n" + 
				"LEFT JOIN cloud_storage.role r ON u.role_id = r.id\n" + 
				"LEFT JOIN cloud_storage.tariff t ON u.tariff_id = t.id\n" + 
				"WHERE u.id = ?", new Object[] {id}, new BeanPropertyRowMapper(User.class));
		return user;
	}

	@Override
	public User getUserByLogin(String login) {
		User user = (User)jdbcTemplate.queryForObject("SELECT u.id,u.name,u.login,u.password,u.email,u.role_id,r.name as role,u.tariff_id,t.name as tariff FROM cloud_storage.user u\n" + 
				"LEFT JOIN cloud_storage.role r ON u.role_id = r.id\n" + 
				"LEFT JOIN cloud_storage.tariff t ON u.tariff_id = t.id\n" + 
				"WHERE u.login = ?", new Object[] {login}, new BeanPropertyRowMapper(User.class));
		
		return user;
	}

	@Override
	public boolean editUser(User user) {
		
		int count = jdbcTemplate.update("UPDATE cloud_storage.user " + 
				"SET name = ?, login = ?, email = ?, tariff_id = ?, role_id = ? WHERE id = ?", new Object[] {user.getName(),user.getLogin(),user.getEmail(),user.getTariff_id(),user.getRole_id(),user.getId()});
		return count > 0;

	}

	@Override
	public UserTariffInfo getUserInfo(Integer id) {
		
		var userTariffInfo = jdbcTemplate.queryForObject("SELECT u.id as userid,t.id,t.name as tariffName,ROUND((\r\n" + 
				"  SELECT SUM(size) FROM cloud_storage.folder AS fo \r\n" + 
				"  LEFT JOIN  cloud_storage.file f ON fo.id=f.folder_id\r\n" + 
				"  WHERE fo.user_id = ?)/1024/t.size,1) AS usedsize FROM cloud_storage.user u" +
				" LEFT JOIN cloud_storage.tariff t ON u.tariff_id = t.id\r\n" + 
				" WHERE u.id = ?", new Object[] {id,id}, new BeanPropertyRowMapper<UserTariffInfo>(UserTariffInfo.class));

		List<OfferDto> offers = jdbcTemplate.query("SELECT id,name,size FROM cloud_storage.tariff", new BeanPropertyRowMapper(OfferDto.class));
		
		userTariffInfo.setOffers(offers);
		
		return userTariffInfo;
	}

}
