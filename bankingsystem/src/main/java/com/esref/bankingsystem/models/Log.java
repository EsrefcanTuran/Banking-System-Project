package com.esref.bankingsystem.models;

import lombok.Data;

@Data
public class Log {
	
	private long id;
	private long accountId;
	private String message;

}
