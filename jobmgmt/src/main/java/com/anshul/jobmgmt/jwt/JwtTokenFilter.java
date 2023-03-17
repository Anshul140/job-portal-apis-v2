package com.anshul.jobmgmt.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.anshul.jobmgmt.entities.Role;
import com.anshul.jobmgmt.entities.User;

import io.jsonwebtoken.Claims;

@Component
@Order(2)
public class JwtTokenFilter extends OncePerRequestFilter{
	
	@Autowired private JwtTokenUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		System.out.println("Authorization header: "+ header);
		
		if(!hasAuthorizationHeader(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String accessToken = getAccessToken(request);
		
		if(!jwtUtil.validateAccessToken(accessToken)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		setAuthenticationContext(accessToken, request);
		filterChain.doFilter(request, response);
	}
	
	private void setAuthenticationContext(String accessToken, HttpServletRequest request) {
		UserDetails userDetails = getUserDetails(accessToken);
		
		UsernamePasswordAuthenticationToken authentication 
		     = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ;
		
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private UserDetails getUserDetails(String accessToken) {
		User userDetails = new User();
		Claims claims = jwtUtil.parseClaims(accessToken);
		
		String claimRoles = (String)claims.get("roles");
		
		System.out.println("claimRoles: "+claimRoles);
		
		claimRoles = claimRoles.replace("[", "").replace("]", "");
		String[] roleNames = claimRoles.split(",");
		
		for(String curr: roleNames) {
			userDetails.addRole(new Role(curr));
		}
		
		String subject = (String) claims.getSubject();
		String[] subjectArray = subject.split(",");
		
		userDetails.setId(Integer.parseInt(subjectArray[0]));
		userDetails.setEmail(subjectArray[1]);
		
		return userDetails;
	}

	private boolean hasAuthorizationHeader(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		// System.out.println("Authorization header: "+ header);
		
		if(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")) {
			return false;
		}
		return true;
	}
	
	private String getAccessToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		String token = header.substring(7);
		System.out.println("Access token: "+token);
		return token;
	}

}
