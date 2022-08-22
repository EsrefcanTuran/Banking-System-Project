package com.esref.bankingsystem.repositories;

import org.springframework.beans.factory.annotation.Autowired;

import com.esref.bankingsystem.mapper.BankMapper;
import com.esref.bankingsystem.models.Bank;

public class MyBatisBankRepository {

	@Autowired
	private BankMapper bankMapper;
	
	public void createbank(Bank b) {
		bankMapper.create(b);
	}
	
}
