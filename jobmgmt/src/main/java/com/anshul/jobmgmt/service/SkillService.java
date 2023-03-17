package com.anshul.jobmgmt.service;

import java.util.List;

import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Skill;

public interface SkillService {

	void addSkillOnProfile(String token, Skill request) throws Exception;
	
	Skill addNewSkillOnDB(Skill request) throws Exception;
	
	List<SkillDto> getAllSkills() throws Exception;
	
	List<SkillDto> getMySkills(String token) throws Exception;
	
	void addSkillOnJob(Integer jobId, Skill request) throws Exception;
}
