package com.anshul.jobmgmt.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Integer id;
	private String name;
	private String email;
	private String company;
	List<SkillDto> skills;
}
