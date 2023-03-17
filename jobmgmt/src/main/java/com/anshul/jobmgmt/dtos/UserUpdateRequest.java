package com.anshul.jobmgmt.dtos;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.anshul.jobmgmt.entities.Skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

	@NotEmpty(message = "Name field cannot be empty! ")
	@Size(min = 2, message = "Name must be of at least 2 characters! ")
	private String name;

	@NotEmpty(message = "Company field cannot be empty! ")
	@Size(min = 2, max = 30, message = "Company name must be between 2 to 30 characters! ")
	private String company;
	
	@NotEmpty(message = "Contact Number cannot be empty! ")
	@Size(min = 4, max = 12 , message = "Contact Number must be between 4 to 12 digits! ")
	private String contactNumber;
//	private String role;
	
	private List<Skill> skills;
}
