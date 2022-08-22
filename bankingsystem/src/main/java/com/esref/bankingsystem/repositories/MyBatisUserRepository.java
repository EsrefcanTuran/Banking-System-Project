package com.esref.bankingsystem.repositories;

import org.springframework.beans.factory.annotation.Autowired;

import com.esref.bankingsystem.mapper.UserMapper;
import com.esref.bankingsystem.models.User;

public class MyBatisUserRepository {

	@Autowired
	private UserMapper userMapper;
	
	public void register(User u) {
		userMapper.register(u);
	}
	
}
