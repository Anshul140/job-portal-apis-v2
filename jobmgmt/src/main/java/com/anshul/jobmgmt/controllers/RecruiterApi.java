package com.anshul.jobmgmt.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.jobmgmt.dtos.JobDto;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.service.RecruiterService;

@RestController
@RequestMapping("/jobs")
public class RecruiterApi {
	
	private RecruiterService recruiterService;
	
	public RecruiterApi(RecruiterService jobService) {
		super();
		this.recruiterService = jobService;
	}
	
	@PostMapping("/{jobId}/trigger-emails")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<String> triggerEmail(
			@PathVariable Integer jobId
	) throws Exception {
		System.out.println("Email Notification-job creation..Method invoked!");
		recruiterService.triggerEmail(jobId);
		return ResponseEntity.ok().body("Email Notifications has been triggered");
	}
	
	@GetMapping
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public List<JobDto> getJobs(
			@RequestHeader(value="Authorization") String token
	) {
//		System.out.println("token: "+token);
       return recruiterService.getAllJobs();
	}
	
	@GetMapping("/my")
	@RolesAllowed({"ROLE_RECRUITER"})
	public List<JobDto> getMyJobs(
			@RequestHeader(value="Authorization") String token
	) {
		return recruiterService.getMyJobs(token);
	}
	
	@PostMapping
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<JobDto> createJob(
			@RequestBody @Valid Job job,
			@RequestHeader(value="Authorization") String token
	) {
		return ResponseEntity.ok(recruiterService.createJob(job, token));
	}
	
	@PutMapping("/{id}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<JobDto> updateJob(
			@RequestBody @Valid Job job,
			@RequestHeader(value="Authorization") String token,
			@PathVariable Integer id
	) throws Exception {
		return ResponseEntity.ok(recruiterService.updateJob(id, job, token));
	}
	
	@DeleteMapping("/delete/{id}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<String> deleteJob(
			@RequestHeader(value="Authorization") String token,
			@PathVariable Integer id
	) throws Exception {
		System.out.println("Delete method invoked");
		recruiterService.deleteJob(id, token);
		return ResponseEntity.ok("Job deleted successfully");
	}
	
	@GetMapping("/{jobId}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<JobDto> getJobById(
			@PathVariable Integer jobId
	) throws Exception {
		return ResponseEntity.ok(recruiterService.getJobById(jobId));
	}
	
	
}
