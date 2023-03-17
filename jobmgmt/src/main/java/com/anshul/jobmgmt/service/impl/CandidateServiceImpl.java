package com.anshul.jobmgmt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.jobmgmt.dtos.ApplicationCreateRequest;
import com.anshul.jobmgmt.dtos.ApplicationDto;
import com.anshul.jobmgmt.dtos.JobDto;
import com.anshul.jobmgmt.entities.Application;
import com.anshul.jobmgmt.entities.ApplicationStatus;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.ApplicationRepository;
import com.anshul.jobmgmt.repositories.JobRepository;
import com.anshul.jobmgmt.repositories.UserRepository;
import com.anshul.jobmgmt.service.CandidateService;

import io.jsonwebtoken.Claims;
import javassist.NotFoundException;

@Service
public class CandidateServiceImpl implements CandidateService{
	
	@Autowired private JwtTokenUtil jwtUtil;
	@Autowired private UserRepository userRepository;
	@Autowired private JobRepository jobRepository;
	@Autowired private ApplicationRepository applicationRepository;
	
	public User getUserFromToken(String token) {
		String accessToken = token.substring(7);
		Claims claims = jwtUtil.parseClaims(accessToken);
		String company = (String) claims.get("company");
		
		String subject = claims.getSubject();
		String[] subjectArray = subject.split(",");
		Integer userId = Integer.parseInt(subjectArray[0]); 
		
		System.out.println("token-id: "+userId);
		User user = userRepository.findById(userId).get();
		
		return user;
	}
	

	@Override
	public ApplicationDto apply(ApplicationCreateRequest request, String token) {
		User candidate = getUserFromToken(token);
		
		System.out.println(candidate.getName());
		Job job = jobRepository.findById(request.getJobId()).orElseThrow();
		
		Application application = new Application();
		
		// add candidate and job to application
		application.setCandidate(candidate);
		application.setJob(job);
		application.setStatus(ApplicationStatus.APPLIED);
		
		//save the application
		Application saved = applicationRepository.save(application);
		
		//convert & return application to dto
		ApplicationDto applicationDto = applicationToDto(saved);
		applicationDto.setCandidateId(candidate.getId());
		applicationDto.setCandidateName(candidate.getName());
		
		System.out.println(applicationDto);
        return applicationDto;
		
		//return null;
	}
	
	public JobDto jobToDto(Job job) {
		JobDto jobDto = new JobDto();
		jobDto.setJobId(job.getId());
		jobDto.setTitle(job.getTitle());
		jobDto.setCtc(job.getCtc());
		jobDto.setLocation(job.getLocation());
		return jobDto;
	}
	
	public ApplicationDto applicationToDto(Application app) {
		ApplicationDto res = new ApplicationDto();
		res.setApplicationId(app.getId());
		
		JobDto jobDto = jobToDto(app.getJob());
		jobDto.setCompany(app.getJob().getUser().getCompany());
		jobDto.setCreatedBy(app.getJob().getUser().getName());
		res.setJobDto(jobDto);
		res.setStatus(app.getStatus());
		
		return res;
	}

	@Override
	public List<ApplicationDto> getAppliedJobs(String token) {
		User candidate = getUserFromToken(token);
		
		List<Application> applications = applicationRepository.findByUserId(candidate.getId());
		
		System.out.println("Name: "+candidate.getName());
		System.out.println("#applications: "+applications.size());
		
		List<ApplicationDto> res = new ArrayList<>();
		
		for(Application app: applications) {
			ApplicationDto curr = applicationToDto(app);
			curr.setCandidateId(candidate.getId());
			curr.setCandidateName(candidate.getName());
			curr.setStatus(app.getStatus());
			res.add(curr);
		}
		
		return res;
		//return null;
	}


	@Override
	public void withdrawApplication(Integer applicationId) throws NotFoundException {
		Application application = applicationRepository.findById(applicationId).get();
		
		if(application != null) {
			application.setCandidate(null);
			application.setJob(null);
			applicationRepository.deleteById(applicationId);
			return;
		}
		else {
			throw new NotFoundException("Application Not Found!");
		}
	}

}
