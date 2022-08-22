package com.esref.bankingsystem.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.esref.bankingsystem.models.Account;

@Mapper
public interface AccountMapper {
	
	public void create(Account a);
	public Account findById(long id);
	public void update(Account a);
	public void delete(Account a);

}
