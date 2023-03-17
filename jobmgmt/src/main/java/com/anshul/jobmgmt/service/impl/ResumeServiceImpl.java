package com.anshul.jobmgmt.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.anshul.jobmgmt.dtos.ResumeDto;
import com.anshul.jobmgmt.dtos.UploadResumeResponse;
import com.anshul.jobmgmt.entities.Resume;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.ResourceNotFoundException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.ResumeRepository;
import com.anshul.jobmgmt.repositories.UserRepository;
import com.anshul.jobmgmt.service.ResumeService;

import io.jsonwebtoken.Claims;

@Service
public class ResumeServiceImpl implements ResumeService{
	
	@Autowired private ResumeRepository resumeRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private JwtTokenUtil jwtUtil;
	
	public User getUserFromToken(String token) {
		String accessToken = token.substring(7);
		Claims claims = jwtUtil.parseClaims(accessToken);
		//String company = (String) claims.get("company");
		
		String subject = claims.getSubject();
		String[] subjectArray = subject.split(",");
		Integer userId = Integer.parseInt(subjectArray[0]); 
		
		System.out.println("token-id: "+userId);
		User user = userRepository.findById(userId).get();
		
		return user;
	}
	

	@Override
	public ResponseEntity<byte[]> saveResume(MultipartFile file, String token) throws Exception {
		
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		User user = getUserFromToken(token);
		
        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence "
                + fileName);
            }
            
            if(!file.isEmpty()) {
            	byte[] data = compress(file.getBytes());
            	
            	Resume resume = new Resume();
            	resume.setFileName(fileName);
            	resume.setFileType(file.getContentType());
            	resume.setCompressedData(data);
            	
            	Resume savedResume = resumeRepository.save(resume);
            	user.setResume(savedResume);
                userRepository.save(user);
                
                byte[] decompressedData = decompress(savedResume.getCompressedData());
                
                return ResponseEntity.ok()
                		.contentType(MediaType.parseMediaType(savedResume.getFileType()))
        	    		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+savedResume.getFileName()+"\"")
        	    		.body(decompressedData);
            }	
            
            throw new Exception("File is empty!");

        } catch (Exception e) {
        	System.out.println(e.getMessage());
            throw new Exception("Could not save File: " + fileName);
        }
	}

	@Override
	public ResponseEntity<byte[]> getResume(String token) throws Exception {
		
		User user = getUserFromToken(token);
		
		if(user.getResume() == null) {
			throw new ResourceNotFoundException("Resume not found! Please upload the resume first to view it!");
//			return null;
		}
		
		Long userResumeId = user.getResume().getId();
		
		Resume resume = resumeRepository.getById(userResumeId);
		
		byte[] decompressedData = decompress(resume.getCompressedData());
		
		return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(resume.getFileType()))
	    		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+resume.getFileName()+"\"")
	    		.body(decompressedData);
	}


	@Override
	public ResponseEntity<byte[]> getResumeByUserId(Integer userId) throws Exception {
		User user = userRepository.getById(userId);
		Long userResumeId = user.getResume().getId();
		
		Resume resume = resumeRepository.getById(userResumeId);
		byte[] data = decompress(resume.getCompressedData());
		 
	    return ResponseEntity.ok()
	    		.contentType(MediaType.parseMediaType(resume.getFileType()))
	    		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+resume.getFileName()+"\"")
	    		.body(data);
	}

	private byte[] compress(byte[] data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream)) {
                gzipStream.write(data);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error compressing byte array", e);
        }
    }

	private byte[] decompress(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            try (GZIPInputStream gzipStream = new GZIPInputStream(inputStream)) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len;
                while ((len = gzipStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error decompressing byte array", e);
        }
    }


	@Override
	public ResumeDto getByUserId(Integer userId) throws Exception {
		User user = userRepository.getById(userId);
		Long userResumeId = user.getResume().getId();
		
		Resume resume = resumeRepository.getById(userResumeId);
		byte[] data = decompress(resume.getCompressedData());
		 
	    return ResumeDto.builder()
	    		.userName(user.getName())
	    		.decodedData(data)
	    		.build();
	    		
	}


	@Override
	public ResponseEntity<byte[]> updateUserResume(Integer userId, MultipartFile file) throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		User user = userRepository.getById(userId);
		
        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence "
                + fileName);
            }
            
            if(!file.isEmpty()) {
            	byte[] data = compress(file.getBytes());
            	
            	Resume resume = user.getResume();
            	resume.setFileName(fileName);
            	resume.setFileType(file.getContentType());
            	resume.setCompressedData(data);
            	
            	user.setResume(resume);
                userRepository.save(user);
                
                byte[] decompressedData = decompress(resume.getCompressedData());
                
                return ResponseEntity.ok()
                		.contentType(MediaType.parseMediaType(resume.getFileType()))
        	    		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+resume.getFileName()+"\"")
        	    		.body(decompressedData);
            }	
            
            throw new Exception("File is empty!");

        } catch (Exception e) {
        	System.out.println(e.getMessage());
            throw new Exception("Could not update File: " + fileName);
        }
		
	}


	@Override
	public String deleteResume(Integer userId) throws Exception {
		User user = userRepository.getById(userId);
		Resume resume = user.getResume();
		user.setResume(null);
		userRepository.save(user);
		
		resumeRepository.deleteById(resume.getId());
		return "Resume Deleted Successfully...";
	}
}
