package com.anshul.jobmgmt.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.jobmgmt.dtos.ApplicationDto;
import com.anshul.jobmgmt.dtos.JobDto;
import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Application;
import com.anshul.jobmgmt.entities.ApplicationStatus;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.ApplicationRepository;
import com.anshul.jobmgmt.repositories.UserRepository;
import com.anshul.jobmgmt.service.ApplicationService;
import com.anshul.jobmgmt.service.EmailSenderService;

import io.jsonwebtoken.Claims;

@Service
public class ApplicationServiceImpl implements ApplicationService{
	
	@Autowired private JwtTokenUtil jwtUtil;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private EmailSenderService emailService;
	
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
	@Transactional
	public void shortlistApplication(Integer applicationId) throws Exception {
		 Application application = 
			applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ResourceNotFoundException("Application not found with id: "+applicationId));
		 
		 application.setStatus(ApplicationStatus.SHORTLISTED);
		 
		 User user = application.getCandidate();
		 Job job = application.getJob();
		 
		 applicationRepository.save(application);
		//Send email to the candidate
		 String to = user.getEmail();
		 String subject = "Application Status for job title: "+job.getTitle();
		 String text = "Dear "+user.getName() + ",\n\n"+
		         "Congratulations! You have been shortlisted for the "+job.getTitle()+" job.\n"+
				 "You will be informed regarding further process shortly. \n\n"+
				 "Thanks and Regards,\n"+
				 "HR Team \n"+
				 ""+job.getUser().getCompany();
		 emailService.sendSimpleEmail(to, text, subject);
	}

	@Override
	public List<ApplicationDto> getShortlistedCandidates(Integer jobId) throws Exception{
		List<Application> applications = 
				applicationRepository.findByJobIdAndApplicationStatus(jobId, ApplicationStatus.SHORTLISTED);
		
		List<ApplicationDto> dtos = new ArrayList<>();
		
		
		for(Application a: applications) {
			List<Skill> skills = a.getCandidate().getSkills();
			List<SkillDto> cand_skills = new ArrayList<>();
			
			for(Skill s: skills) {
				cand_skills.add(skillToDto(s));
			}
			
			ApplicationDto adt = applicationToDto(a);
			adt.setCandidateId(a.getCandidate().getId());
			adt.setCandidateName(a.getCandidate().getName());
			adt.setCandidateCurrCompany(a.getCandidate().getCompany());
			adt.setCandidateSkills(cand_skills);
			dtos.add(adt);
		}
		
		return dtos;
	}

	@Override
	public List<ApplicationDto> getAllApplications(String token) throws Exception {
		User user = getUserFromToken(token);
		
		Integer userId = user.getId();
		List<Application> applications = applicationRepository.findAllByRecruiterId(userId);
		
		List<ApplicationDto> dtos = new ArrayList<>();
		
		for(Application a: applications) {
			
			List<Skill> skills = a.getCandidate().getSkills();
			List<SkillDto> cand_skills = new ArrayList<>();
			
			for(Skill s: skills) {
				cand_skills.add(skillToDto(s));
			}
			
			ApplicationDto adt = applicationToDto(a);
			adt.setCandidateId(a.getCandidate().getId());
			adt.setCandidateName(a.getCandidate().getName());
			adt.setCandidateCurrCompany(a.getCandidate().getCompany());
			adt.setCandidateSkills(cand_skills);
			dtos.add(adt);
		}
		
		return dtos;
	}

	@Override
	public List<ApplicationDto> getAllApplicationsByJobId(Integer jobId) {
		List<Application> applications = applicationRepository.findByJobId(jobId);
		
		List<ApplicationDto> dtos = new ArrayList<>();
		
		for(Application a: applications) {
			ApplicationDto adt = applicationToDto(a);
			adt.setCandidateId(a.getCandidate().getId());
			adt.setCandidateName(a.getCandidate().getName());
			adt.setCandidateCurrCompany(a.getCandidate().getCompany());
			dtos.add(adt);
		}
		
		return dtos;
	}
	
	public SkillDto skillToDto(Skill s) {
		return SkillDto.builder()
				.skillId(s.getId())
				.skillName(s.getName())
				.build();
	}

}
