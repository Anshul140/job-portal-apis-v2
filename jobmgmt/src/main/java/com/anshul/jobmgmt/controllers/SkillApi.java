package com.anshul.jobmgmt.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.service.SkillService;

@RestController
@RequestMapping("/skills")
public class SkillApi {

	@Autowired private SkillService skillService;
	
	@PostMapping("/addOnProfile")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<?> addOnProfile(
			@RequestHeader(value = "Authorization") String token,
			@RequestBody Skill request
	) throws Exception {
	    skillService.addSkillOnProfile(token, request);
	    return ResponseEntity.ok().
	    		body("Skill-"+ request.getName() +" added on your profile successfully");
	}
	
	@PostMapping("/addOnJob/{jobId}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<?> addOnJob(
			@RequestBody Skill request,
			@PathVariable Integer jobId
	) throws Exception {
	    skillService.addSkillOnJob(jobId, request);
	    return ResponseEntity.ok().
	    		body("Skill-"+ request.getName() +" added on job successfully");
	}
	
	@PostMapping("/addOnDB")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<?> addOnDB(@RequestBody Skill request) throws Exception {
		skillService.addNewSkillOnDB(request);
		return ResponseEntity.ok().
	    		body("Skill-"+ request.getName() +" added on Database successfully");
	}
	
	@GetMapping("/getMySkills")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<List<SkillDto>> getMySkills(
			@RequestHeader(value = "Authorization") String token  
	) throws Exception {
		return ResponseEntity.ok(skillService.getMySkills(token));
	}
	
	@GetMapping("/getAllSkills")
	public ResponseEntity<List<SkillDto>> getAllSkills() throws Exception {
		return ResponseEntity.ok(skillService.getAllSkills());
	}
}
