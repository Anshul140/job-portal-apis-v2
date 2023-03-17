package com.anshul.jobmgmt.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

public class AuthRequest {

	@NotEmpty(message = "Email cannot be empty! ")
	@Email(message = "Invalid Email address! ") 
	private String email;
	
	@NotEmpty(message = "Password cannot be empty! ")
	@Size(min = 4, max = 15 , message = "Password must be between 4 to 15 characters! ")
	private String password;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
