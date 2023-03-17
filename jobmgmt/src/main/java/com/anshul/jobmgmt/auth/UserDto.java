package com.anshul.jobmgmt.auth;

import java.util.List;

import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	
	private Integer id;
	private String email;
	private String name;
	private String role;
	private String company;
	private String contactNumber;
	List<SkillDto> skills;
}
