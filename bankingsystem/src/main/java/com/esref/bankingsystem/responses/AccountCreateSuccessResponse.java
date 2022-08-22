package com.esref.bankingsystem.responses;

import lombok.Data;

@Data
public class AccountCreateSuccessResponse {
	
	private String message;
	private long accountNumber;

}
