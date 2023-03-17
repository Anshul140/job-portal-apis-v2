package com.anshul.jobmgmt.service;

import java.util.List;

import com.anshul.jobmgmt.dtos.JobDto;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;

public interface RecruiterService {

	List<JobDto> getAllJobs();
	
	JobDto createJob(Job job, String accessToken);
	
	JobDto updateJob(Integer id, Job job, String token) throws Exception;
	
	void deleteJob(Integer id, String token) throws Exception;
	
	JobDto getJobById(Integer id) throws ResourceNotFoundException;
	
	List<JobDto> getMyJobs(String token);
	
	void triggerEmail(Integer jobId) throws Exception;
}
