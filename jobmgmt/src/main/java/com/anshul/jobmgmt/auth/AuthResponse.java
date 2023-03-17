package com.anshul.jobmgmt.auth;

import java.util.Set;

import com.anshul.jobmgmt.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String token;	
	private UserDto userDetails;
}
