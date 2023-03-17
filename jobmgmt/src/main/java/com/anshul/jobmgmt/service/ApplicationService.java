package com.anshul.jobmgmt.service;

import java.util.List;

import com.anshul.jobmgmt.dtos.ApplicationDto;

public interface ApplicationService {

	void shortlistApplication(Integer applicationId) throws Exception;
	
	List<ApplicationDto> getShortlistedCandidates(Integer jobId) throws Exception;
	
	List<ApplicationDto> getAllApplications(String token) throws Exception;

	List<ApplicationDto> getAllApplicationsByJobId(Integer jobId);
}
