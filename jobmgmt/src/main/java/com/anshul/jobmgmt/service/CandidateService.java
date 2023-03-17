package com.anshul.jobmgmt.service;

import java.util.List;

import com.anshul.jobmgmt.dtos.ApplicationCreateRequest;
import com.anshul.jobmgmt.dtos.ApplicationDto;

import javassist.NotFoundException;

public interface CandidateService {
    
	ApplicationDto apply(ApplicationCreateRequest req, String token);
	
	List<ApplicationDto> getAppliedJobs(String token);

	void withdrawApplication(Integer applicationId) throws NotFoundException;
}
