package com.anshul.jobmgmt.auth;

import java.util.List;

import com.anshul.jobmgmt.dtos.SkillDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponse {

	private Integer id;
	private String name;
	private String email;
	private String company;
	private String contactNumber;
	List<SkillDto> skills;
}
