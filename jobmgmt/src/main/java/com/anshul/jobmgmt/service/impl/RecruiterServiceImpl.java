package com.anshul.jobmgmt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.jobmgmt.dtos.JobDto;
import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.entities.JobStatus;
import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.exception.UnauthorizedRequestException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.JobRepository;
import com.anshul.jobmgmt.repositories.SkillRepository;
import com.anshul.jobmgmt.repositories.UserRepository;
import com.anshul.jobmgmt.service.EmailSenderService;
import com.anshul.jobmgmt.service.RecruiterService;

import io.jsonwebtoken.Claims;

@Service
public class RecruiterServiceImpl implements RecruiterService{

	private final JobRepository jobRepository;
	private final UserRepository userRepository;
	@Autowired private SkillRepository skillRepository;
	@Autowired private EmailSenderService emailService;
	
	@Autowired JwtTokenUtil jwtUtil;
	
    public RecruiterServiceImpl(JobRepository jobRepository, UserRepository userRepository) {
    	super();
    	this.jobRepository = jobRepository;
		this.userRepository = userRepository ;
    }
    
    public SkillDto skillToDto(Skill S) {
    	return SkillDto.builder()
    			.skillName(S.getName())
    			.skillId(S.getId())
    			.build();
    }
    
	@Override
	public List<JobDto> getAllJobs() {
		List<Job> jobs = jobRepository.findByStatus(JobStatus.ACTIVE);
        List<JobDto> jobDtos = new ArrayList<>();
        
        for(Job job: jobs) {
        	User recruiter = job.getUser();
        	JobDto curr = jobToDto(job);
        	curr.setCompany(recruiter.getCompany());
        	curr.setCreatedBy(recruiter.getName());
        	jobDtos.add(curr);
        	
        }
		return jobDtos;
	}

	@Override
	public JobDto updateJob(Integer sid, Job request, String token) throws Exception {
		Integer jobId = sid;
		
		User user = getUserFromToken(token);
		
		Integer f = 0, z = 0;
		List<Job> jobs = jobRepository.findJobsByRecruiterId(user.getId(), JobStatus.ACTIVE); 
		//System.out.println("Jobs-size: "+jobs.size());
		
	    for(Job j: jobs) {
	    	if(j.getId() == jobId) {
	    		f = 1;
	    		break;
	    	}
	    }
		
	    // System.out.println("f: "+f+" z: "+z);
	    if(f == z) {
	    	throw new UnauthorizedRequestException("",user.getId());
	    }
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException(jobId));
		
		for(Job j: jobs) {
	    	if(j.getId() == jobId) {
	    		job.setTitle(request.getTitle());
	    		job.setCtc(request.getCtc());
	    		job.setLocation(request.getLocation());
	    		job.setRequiredSkills(request.getRequiredSkills());
	    		jobRepository.save(job);
	    	}
	    }
		
		userRepository.save(user);
		JobDto updatedJobDto = jobToDto(job);
		updatedJobDto.setCompany(user.getCompany());
		updatedJobDto.setCreatedBy(user.getName());
		
		return updatedJobDto;
	}

	@Override
	public void deleteJob(Integer id, String token) throws Exception {
		System.out.println("Delete method invoked");
		User rec = getUserFromToken(token);
		
		Integer f = 0, z = 0;
		List<Job> jobs = jobRepository.findJobsByRecruiterId(rec.getId(), JobStatus.ACTIVE); 
	
	    for(Job j: jobs) {
	    	if(j.getId() == id) {
	    		f = 1;
	    		break;
	    	}
	    }
		
	    if(f == z) {
	    	throw new UnauthorizedRequestException("",id);
	    }
		
		
		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		
		job.setUser(null);
		job.getRequiredSkills().clear();
		job.setCtc(null);
		job.setTitle(null);
		job.getApplications().clear();
		job.setStatus(JobStatus.DEAD);
		job = jobRepository.save(job);
	}

	@Override
	public JobDto getJobById(Integer id) throws ResourceNotFoundException {
		Optional<Job> result = jobRepository.findById(id);
		if(result.isPresent()) {
			return jobToDto(result.get());
		} else {
			throw new ResourceNotFoundException(id);
		}
	}

	@Override
	public JobDto createJob(Job job, String token) {
		
		User recruiter = getUserFromToken(token);
		job.setStatus(JobStatus.ACTIVE);
		
		job.setUser(recruiter);
		
		Job savedjob = jobRepository.save(job);
		//recruiter.addJobs(updatedjob);
		//userRepository.save(recruiter);
		
		JobDto updatedDtoJob =  jobToDto(savedjob);
		updatedDtoJob.setCompany(recruiter.getCompany());
		updatedDtoJob.setCreatedBy(recruiter.getName());
		
		return updatedDtoJob;
	}
	
	public JobDto jobToDto(Job job) {
		
		List<SkillDto> sdto = new ArrayList<>();
		List<Skill> reqd = job.getRequiredSkills();
		
		for(Skill s: reqd) {
			sdto.add(skillToDto(s));
		}
		
		JobDto jobDto = new JobDto();
		jobDto.setJobId(job.getId());
		jobDto.setTitle(job.getTitle());
		jobDto.setCtc(job.getCtc());
		jobDto.setLocation(job.getLocation());
		jobDto.setSkillsRequired(sdto);
		return jobDto;
	}
	
	public User getUserFromToken(String token) {
		String accessToken = token.substring(7);
		Claims claims = jwtUtil.parseClaims(accessToken);
		String company = (String) claims.get("company");
		
		String subject = claims.getSubject();
		String[] subjectArray = subject.split(",");
		Integer userId = Integer.parseInt(subjectArray[0]);
		User user = userRepository.findById(userId).get();
		
		return user;
	}

	@Override
	public void triggerEmail(Integer jobId) throws Exception{
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("No job found with id: "+jobId));
		
		List<Skill> reqSkills = job.getRequiredSkills();
		System.out.println("#Required skills for job: "+reqSkills.size());
		
		for(Skill s: reqSkills) {
			List<User> users = s.getUsers();
			for(User u: users) {
				String to = u.getEmail();
				String subject = "New job posted matching your skills";
				String content = 
						"A new job has been posted that matches your skills: "+job.getTitle()+".\n"
						+"Check out this new job.\n\n"
						+"Thanks & Regards\n"
						+"HR Team"
						+job.getUser().getCompany();
				emailService.sendSimpleEmail(to, content, subject);
			}
		}
		
	}

	@Override
	public List<JobDto> getMyJobs(String token) {
		
		User user = getUserFromToken(token);
		List<Job> myJobs = jobRepository.findJobsByRecruiterId(user.getId(), JobStatus.ACTIVE); 
		
		System.out.println("size-myJobs: "+myJobs.size());
		List<JobDto> jobDtos = new ArrayList<>();
        
        for(Job job: myJobs) {
        	User recruiter = job.getUser();
        	JobDto curr = jobToDto(job);
        	curr.setCompany(recruiter.getCompany());
        	curr.setCreatedBy(recruiter.getName());
        	jobDtos.add(curr);
        	
        }
		return jobDtos;
	}
	
}
