package com.esref.bankingsystem.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esref.bankingsystem.exchange.Exchange;
import com.esref.bankingsystem.mapper.AccountMapper;
import com.esref.bankingsystem.mapper.LogMapper;
import com.esref.bankingsystem.models.Account;
import com.esref.bankingsystem.models.Log;

@Component
public class MyBatisAccountRepository implements IAccountRepository{

	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private LogMapper logMapper;
	
	@Autowired
	private Exchange exchanger;
	
	@Override
	public Account create(String name, String surname, String email, String tc, String type) {
		if (type.equals("TL") || type.equals("Dolar") || type.equals("Altın")) {
			long accountNumber = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
			Account a = new Account();
			a.setAccountNumber(accountNumber);
			a.setName(name);
			a.setSurname(surname);
			a.setEmail(email);
			a.setTc(tc);
			a.setType(type);
			a.setBalance(0);
			a.setLastModified(System.currentTimeMillis());
			a.setDeleted(false);
			accountMapper.create(a);
			return a;
		}
		return null;
	}

	@Override
	public Account updateAccount(Account a) {
		a.setLastModified(System.currentTimeMillis());
		accountMapper.update(a);
		return a;
	}

	@Override
	public Account findByAccountId(long accountId) {
		return accountMapper.findById(accountId);
	}

	@Override
	public boolean transfer(double amount, long ownerAccountId, long transferAccountId) {
		Account ownerAccount = this.findByAccountId(ownerAccountId);
		Account transferAccount = this.findByAccountId(transferAccountId);
		if (ownerAccount.getBalance() < amount) {
			return false;
		}
		double transferAmount = amount;
		if (!ownerAccount.getType().equals(transferAccount.getType())) {
			transferAmount = this.exchanger.exchange(amount, ownerAccount.getType(), transferAccount.getType());
		}
		transferAccount.setBalance(transferAccount.getBalance() + transferAmount);
		ownerAccount.setBalance(ownerAccount.getBalance() - amount);
		this.updateAccount(ownerAccount);
		this.updateAccount(transferAccount);
		return true;
	}

	@Override
	public Account deposit(long accountNumber, double amount) {
		Account a = this.findByAccountId(accountNumber);
		a.setBalance(amount + a.getBalance());
		this.updateAccount(a);
		return a;
	}

	@Override
	public List<Log> transactionLogs(long accountId) {
		return logMapper.getLogsByAccountId(accountId);
	}

	@Override
	public void deleteAccount(Account a) {
		accountMapper.delete(a);
	}

}
