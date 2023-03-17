package com.anshul.jobmgmt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.JobRepository;
import com.anshul.jobmgmt.repositories.SkillRepository;
import com.anshul.jobmgmt.repositories.UserRepository;
import com.anshul.jobmgmt.service.SkillService;

import io.jsonwebtoken.Claims;

@Service
public class SkillServiceImpl implements SkillService{
	
	@Autowired private JwtTokenUtil jwtUtil;
	@Autowired private UserRepository userRepository;
	@Autowired private JobRepository jobRepository;
	@Autowired private SkillRepository skillRepository;
	
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
	
	public SkillDto skillToDto(Skill s) {
		return SkillDto.builder()
				.skillId(s.getId())
				.skillName(s.getName())
				.build();
	}

	@Override
	public void addSkillOnProfile(String token, Skill request) throws Exception {
		User user = getUserFromToken(token);
		List<Skill> skills = user.getSkills();
		skills.add(request);
		user.setSkills(skills);
		
		// user.addSkill(request);
		
		System.out.println("after adding skills[] size: "+skills.size());
		userRepository.save(user);
	}

	@Override
	public Skill addNewSkillOnDB(Skill request) throws Exception {
		Skill newSkill = skillRepository.save(request);
		
		return newSkill;
	}

	@Override
	public List<SkillDto> getAllSkills() throws Exception {
		List<Skill> allSkills= skillRepository.findAll();
		List<SkillDto> res = new ArrayList<>();
		
		for(Skill s: allSkills) {
			res.add(skillToDto(s));
		}
		
		return res;
	}

	@Override
	public List<SkillDto> getMySkills(String token) throws Exception {
		User user = getUserFromToken(token);
		
		System.out.println(user.getName());
		List<Skill> userSkills = user.getSkills();
		System.out.println("Skills[] size: " +userSkills.size());
		
		List<SkillDto> res = new ArrayList<>();
		
		for(Skill s: userSkills) {
			res.add(skillToDto(s));
		}
		
		return res;
	}

	@Override
	public void addSkillOnJob(Integer jobId, Skill request) throws Exception {
		// TODO Auto-generated method stub
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("No job found with given id: "+jobId));
	    
		List<Skill> reqSkills = job.getRequiredSkills();
		reqSkills.add(request);
		jobRepository.save(job);
	}

}
