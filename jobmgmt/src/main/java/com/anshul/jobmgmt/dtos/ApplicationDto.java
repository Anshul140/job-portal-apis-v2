package com.anshul.jobmgmt.dtos;

import java.util.List;

import com.anshul.jobmgmt.entities.ApplicationStatus;
import com.anshul.jobmgmt.entities.Job;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Setter
public class ApplicationDto {
	public ApplicationDto() {
	}
	private Integer applicationId;
	private Integer candidateId;
	private String candidateName;
	private String candidateCurrCompany;
	private List<SkillDto> candidateSkills;
	private JobDto jobDto;
	private ApplicationStatus status;
}
