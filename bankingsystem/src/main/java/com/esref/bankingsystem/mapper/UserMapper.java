package com.esref.bankingsystem.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.esref.bankingsystem.models.User;

@Mapper
public interface UserMapper {
	
	public void register(User u);

}
