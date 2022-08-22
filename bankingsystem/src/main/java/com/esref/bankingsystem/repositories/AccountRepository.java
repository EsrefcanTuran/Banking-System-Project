package com.esref.bankingsystem.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.esref.bankingsystem.exchange.Exchange;
import com.esref.bankingsystem.models.Account;
import com.esref.bankingsystem.models.Log;


public class AccountRepository implements IAccountRepository{

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
			String fileFormat = a.toFileFormat();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(accountNumber + ".txt")));
				bw.write(fileFormat);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return a;
		}
		return null;
	}
	
	@Override
	public Account updateAccount(Account a) {

		a.setLastModified(System.currentTimeMillis());
		String fileFormat = a.toFileFormat();
		File f = new File(a.getAccountNumber() + ".txt");
		f.delete();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(fileFormat);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	@Override
	public Account findByAccountId(long accountNumber) {
		
		File f = new File(accountNumber + ".txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String accountDetailString = br.readLine();
			String[] parts = accountDetailString.split(",");
			Account a = new Account();
			a.setAccountNumber(Long.parseLong(parts[0]));
			a.setName(parts[1]);
			a.setSurname(parts[2]);
			a.setEmail(parts[3]);
			a.setTc(parts[4]);
			a.setType(parts[5]);
			a.setBalance(Double.parseDouble(parts[6]));
			a.setLastModified(Long.valueOf(parts[7]));
			br.close();
			return a;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean transfer(double amount, long ownerAccountNumber, long transferAccountNumber) {
		
		Account ownerAccount = this.findByAccountId(ownerAccountNumber);
		Account transferAccount = this.findByAccountId(transferAccountNumber);
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
	public List<Log> transactionLogs(long accountNumber) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("logs.txt"));
			List<Log> list = new ArrayList<Log>();
			String s;
			while ((s = br.readLine()) != null) {
				if (s.startsWith(String.valueOf(accountNumber))) {
					Log l = new Log();
					if (s.contains("deposit")) {
						l.setAccountId(accountNumber);
						l.setMessage(" nolu hesaba " + s.substring(26) + " " + this.findByAccountId(accountNumber).getType() + " yatırılmıştır.");
					} else {
						int firstColon = s.indexOf(":");
						int secondColon = s.indexOf(":", firstColon + 1);
						int comma = s.indexOf(",");
						l.setAccountId(accountNumber);
						l.setMessage(" nolu hesaptan " + s.substring(secondColon + 1) + " nolu hesaba " + s.substring(firstColon + 1, comma) + " " + this.findByAccountId(accountNumber).getType() + " transfer edilmiştir.");
					}
				}
			}
			br.close();
			return list;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteAccount(Account a) {
		// TODO Auto-generated method stub
		
	}
	

}
