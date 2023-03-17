package com.anshul.jobmgmt.controllers;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anshul.jobmgmt.dtos.ResumeDto;
import com.anshul.jobmgmt.dtos.UploadResumeResponse;
import com.anshul.jobmgmt.entities.Resume;
import com.anshul.jobmgmt.service.ResumeService;

@RestController
@RequestMapping("/resume")
public class ResumeApi {

	@Autowired private ResumeService resumeService;
	
	@PostMapping("/upload")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<byte[]> upload(
			@RequestParam("file") MultipartFile file,
			@RequestHeader(value = "Authorization") String token
	) throws Exception {
		return resumeService.saveResume(file, token);
	}
	
	@GetMapping("/showMyResume")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<byte[]> showMyResume(
			@RequestHeader(value = "Authorization") String token
	) throws Exception{
		
		return resumeService.getResume(token); 
	}
	
	@GetMapping("/{userId}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<byte[]> showResumeByUserId(
			@PathVariable("userId") Integer userId
	) throws Exception {
		return resumeService.getResumeByUserId(userId);
	}
	
	@GetMapping("/get/{userId}")
	@RolesAllowed({"ROLE_RECRUITER"})
	public ResponseEntity<ResumeDto> getByUserId(
			@PathVariable("userId") Integer userId
	) throws Exception {
		return ResponseEntity.ok(resumeService.getByUserId(userId));
	}
	
	@PutMapping("/{userId}")
	@RolesAllowed({"ROLE_CANDIDATE","ROLE_RECRUITER"})
	public ResponseEntity<byte[]> updateUserResume(
			@RequestParam("file") MultipartFile file,
			@PathVariable Integer userId
	) throws Exception{
		return resumeService.updateUserResume(userId, file);
	}
	
	@DeleteMapping("/{userId}")
	@RolesAllowed({"ROLE_CANDIDATE","ROLE_RECRUITER"})
	public ResponseEntity<String> deleteResume(
			@PathVariable Integer userId
	) throws Exception {
		return ResponseEntity.ok(resumeService.deleteResume(userId));
	}
}
