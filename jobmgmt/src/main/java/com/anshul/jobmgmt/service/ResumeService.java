package com.anshul.jobmgmt.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.anshul.jobmgmt.dtos.ResumeDto;
import com.anshul.jobmgmt.dtos.UploadResumeResponse;
import com.anshul.jobmgmt.entities.Resume;

public interface ResumeService {

    ResponseEntity<byte[]> saveResume(MultipartFile file, String token) throws Exception;
	
	ResponseEntity<byte[]> getResume(String token) throws Exception;
	
	ResponseEntity<byte[]> getResumeByUserId(Integer userId) throws Exception;
	
	ResumeDto getByUserId(Integer userId) throws Exception;
	
	ResponseEntity<byte[]> updateUserResume(Integer userId, MultipartFile file) throws Exception;
	
	String deleteResume(Integer userId) throws Exception;
}
