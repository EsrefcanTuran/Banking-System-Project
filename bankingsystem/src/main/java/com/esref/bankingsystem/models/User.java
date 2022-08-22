package com.esref.bankingsystem.models;

import lombok.Data;

@Data
public class User {
	
	private String username;
	private String email;
	private String password;
	private boolean enabled;
	private String authorities;

}
