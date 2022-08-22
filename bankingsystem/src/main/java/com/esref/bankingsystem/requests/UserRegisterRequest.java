package com.esref.bankingsystem.requests;

import lombok.Data;

@Data
public class UserRegisterRequest {
	
	private String username;
	private String email;
	private String password;
	
}
