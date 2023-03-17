package com.anshul.jobmgmt.controllers;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.jobmgmt.auth.AuthRequest;
import com.anshul.jobmgmt.auth.AuthResponse;
import com.anshul.jobmgmt.auth.RegistrationRequest;
import com.anshul.jobmgmt.auth.RegistrationResponse;
import com.anshul.jobmgmt.auth.UserDto;
import com.anshul.jobmgmt.dtos.ApplicationDto;
import com.anshul.jobmgmt.dtos.UserUpdateRequest;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserApi {
	
	@Autowired private UserService userService;
	@Autowired JwtTokenUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<RegistrationResponse> register(
			@Valid @RequestBody RegistrationRequest request
	) {
		return ResponseEntity.ok(userService.register(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
		try {
			return ResponseEntity.ok(userService.login(request));
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PutMapping("/update/{userId}")
	@RolesAllowed({"ROLE_CANDIDATE", "ROLE_RECRUITER"})
	public ResponseEntity<UserDto> updateProfile(
			@RequestHeader(value="Authorization") String token,
			@Valid @RequestBody UserUpdateRequest request,
			@PathVariable Integer userId
	) throws Exception {
		return ResponseEntity.ok(userService.updateProfile(userId, token, request));
	}
	
}
