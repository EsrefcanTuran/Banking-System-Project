package com.esref.bankingsystem.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.esref.bankingsystem.models.Bank;

@Mapper
public interface BankMapper {
	
	public void create(Bank b);

}
