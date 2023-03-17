package com.anshul.jobmgmt.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.jobmgmt.dtos.ApplicationDto;
import com.anshul.jobmgmt.entities.Application;
import com.anshul.jobmgmt.service.ApplicationService;

@RestController
@RequestMapping("/jobs")
public class ApplicationApi {
	
	private final ApplicationService applicationService;
	
	public ApplicationApi(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@GetMapping("/{jobId}/applications")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<List<ApplicationDto>> getAllApplicationsByJobId(
			@PathVariable Integer jobId
	) throws Exception {
		return ResponseEntity.ok(applicationService.getAllApplicationsByJobId(jobId));
	}

	@GetMapping("/{jobId}/shortlistedApplications")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<List<ApplicationDto>> getShortlistedApplications(
			@PathVariable Integer jobId
	) throws Exception {
		return ResponseEntity.ok(applicationService.getShortlistedCandidates(jobId));
	}
	
	
	@PostMapping("/{applicationId}/shortlist")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<String> shortlistApplication(
			@PathVariable Integer applicationId
	) throws Exception {
		applicationService.shortlistApplication(applicationId);
		return ResponseEntity.ok().body("Application has been shortlisted");
	}
	
	@GetMapping("/allApplications")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<List<ApplicationDto>> getMyApplications(
			@RequestHeader(value = "Authorization") String token
	) throws Exception {
		return ResponseEntity.ok(applicationService.getAllApplications(token));
	}
}
