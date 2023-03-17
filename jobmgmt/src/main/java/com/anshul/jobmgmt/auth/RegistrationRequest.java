package com.anshul.jobmgmt.auth;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.anshul.jobmgmt.entities.Skill;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequest {
	
	@NotEmpty(message="Name field cannot be empty! ")
	@Size(min = 2, message = "Name must be of at least 2 characters! ")
	private String name;
	
	@NotEmpty(message = "Email field cannot be empty! ")
	@Email(message = "Invalid email address! ")
	private String email;
	
	@NotEmpty(message = "Password field cannot be empty! ")
	@Size(min = 4, max = 15 , message = "Password must be between 4 to 15 characters! ")
	private String password;
	
	@NotEmpty(message = "Contact Number field cannot be empty! ")
	@Size(min = 4, max = 12 , message = "Contact Number must be between 4 to 12 digits! ")
	private String contactNumber;
	
	@NotEmpty(message = "Company field cannot be empty! ")
	@Size(min = 2, max = 30, message = "Company name must be between 2 to 30 characters! ")
	private String company;
	
	@NotNull
	private String role;
	
	List<Skill> skills;
}
