package com.anshul.jobmgmt.dtos;

import java.util.List;

import com.anshul.jobmgmt.entities.Skill;

import lombok.Data;

@Data
public class JobDto {
	private Integer jobId;
	private String title;
	private String company;
	private String ctc;
	private String location;
	private String createdBy;
	private List<SkillDto> skillsRequired;
}
