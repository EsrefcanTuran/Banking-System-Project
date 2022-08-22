package com.esref.bankingsystem.repositories;

import java.util.List;

import com.esref.bankingsystem.models.Account;
import com.esref.bankingsystem.models.Log;

public interface IAccountRepository {
	
	public Account create(String name, String surname, String email, String tc, String type);
	
	public Account updateAccount(Account a);
	
	public Account findByAccountId(long accountId);
	
	public boolean transfer(double amount, long ownerAccountId, long transferAccountId);
	
	public Account deposit(long accountId, double amount);
	
	public List<Log> transactionLogs(long accountId);
	
	public void deleteAccount(Account a);

}
