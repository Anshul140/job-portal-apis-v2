package com.anshul.jobmgmt.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.jobmgmt.dtos.ApplicationCreateRequest;
import com.anshul.jobmgmt.dtos.ApplicationDto;
import com.anshul.jobmgmt.service.CandidateService;

import javassist.NotFoundException;

@RestController
@RequestMapping("/jobs")
public class CandidateApi {

	@Autowired
	private CandidateService candidateService;
	
	@PostMapping("/apply")
	@RolesAllowed({"ROLE_CANDIDATE"})
	public ResponseEntity<ApplicationDto> apply(
			@RequestHeader(value="Authorization") String token,
			@RequestBody ApplicationCreateRequest request
	) {
		
		return ResponseEntity.ok(candidateService.apply(request, token));
	}
	 
	@GetMapping("/applied")
	@RolesAllowed({"ROLE_CANDIDATE"})
	public List<ApplicationDto> getAppliedJobs(
			@RequestHeader(value="Authorization") String token
	) {
		return candidateService.getAppliedJobs(token);
	}
	
	@DeleteMapping("/{applicationId}")
	@RolesAllowed({"ROLE_CANDIDATE"})
	public ResponseEntity<String> withdrawAppliaction(
			@PathVariable Integer applicationId
	) throws NotFoundException {
		candidateService.withdrawApplication(applicationId);
		return ResponseEntity.ok("Application withdrawn successfully");
	}
}
