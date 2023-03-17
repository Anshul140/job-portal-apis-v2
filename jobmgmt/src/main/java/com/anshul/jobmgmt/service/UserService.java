package com.anshul.jobmgmt.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anshul.jobmgmt.auth.AuthRequest;
import com.anshul.jobmgmt.auth.AuthResponse;
import com.anshul.jobmgmt.auth.RegistrationRequest;
import com.anshul.jobmgmt.auth.RegistrationResponse;
import com.anshul.jobmgmt.auth.UserDto;
import com.anshul.jobmgmt.dtos.SkillDto;
import com.anshul.jobmgmt.dtos.UserUpdateRequest;
import com.anshul.jobmgmt.entities.Role;
import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.entities.User;
import com.anshul.jobmgmt.exception.UnauthorizedRequestException;
import com.anshul.jobmgmt.jwt.JwtTokenUtil;
import com.anshul.jobmgmt.repositories.RoleRepository;
import com.anshul.jobmgmt.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import javassist.tools.web.BadHttpRequest;


@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired AuthenticationManager authManager;
	
	@Autowired private JwtTokenUtil jwtUtil;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Value("${app.jwt.secret}")
	private String secret;
	
	public SkillDto skillToDto(Skill S) {
    	return SkillDto.builder()
    			.skillName(S.getName())
    			.skillId(S.getId())
    			.build();
    }
    

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username).get();
		if(user == null) {
			throw new UsernameNotFoundException("User not found!");
		}
		
		return new org.springframework.security.core.userdetails
				.User(user.getUsername(), user.getPassword(), user.getAuthorities());
	}

	private Set<GrantedAuthority> getAuthorities(Set<Role> roles) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		
		for(Role role: roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}	
		return authorities;
	}
	
	public AuthResponse login(AuthRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);
		
		User currUser = userRepository.findByEmail(request.getEmail()).get();
		
//		System.out.println("User Details..: ");
		
		
		String accessToken = jwtUtil.generateAccessToken(currUser);
		
//		System.out.println("AccessToken: "+accessToken);
		
		UserDto userDto = userToDto(currUser);
		
		System.out.println("UserDto done...");
		return AuthResponse.builder()
				.token(accessToken)
				.userDetails(userDto)
				.build();
		
//		AuthResponse rs = new AuthResponse();
//		return rs;
	}
	
	public RegistrationResponse register(RegistrationRequest request) {
		String role = request.getRole();
		
		if(!role.equals("candidate") && !role.equals("recruiter")) {
			throw new IllegalArgumentException("Invalid User Role!");
		}
		
		List<Skill> skills = request.getSkills();
		List<SkillDto> sdto = new ArrayList<>();
		
		for(Skill s: skills) {
			sdto.add(skillToDto(s));
		}
		
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder().encode(request.getPassword()));
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findByName("ROLE_" + role.toUpperCase()));
		user.setRoles(roles);
		user.setCompany(request.getCompany());
		user.setContactNumber(request.getContactNumber());
		user.setSkills(request.getSkills());
		
		User savedUser = userRepository.save(user);
		return RegistrationResponse.builder()
				.id(savedUser.getId())
				.name(savedUser.getName())
				.email(savedUser.getEmail())
				.company(savedUser.getCompany())
				.contactNumber(savedUser.getContactNumber())
				.skills(sdto)
				.build();
	}
	
	private PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public UserDto userToDto(User u) {
		
		List<Skill> skills = u.getSkills();
		List<SkillDto> sdto = new ArrayList<>();
		
		System.out.println("Skills-size: "+skills.size());
		
		for(Skill s: skills) {
			sdto.add(skillToDto(s));
		}
		
		return UserDto.builder()
				.id(u.getId())
				.name(u.getName())
				.email(u.getEmail())
				.company(u.getCompany())
				.role(u.getRoles().stream().findFirst().get().getName())
				.contactNumber(u.getContactNumber())
				.skills(sdto)
				.build();
	}
	
	public User getUserFromToken(String token) {
		String accessToken = token.substring(7);
		Claims claims = jwtUtil.parseClaims(accessToken);
		String company = (String) claims.get("company");
		
		String subject = claims.getSubject();
		String[] subjectArray = subject.split(",");
		Integer userId = Integer.parseInt(subjectArray[0]);
		User user = userRepository.findById(userId).get();
		
		return user;
	}
	
	public UserDto updateProfile(Integer userId, String token, UserUpdateRequest request) throws UnauthorizedRequestException {
		User user = getUserFromToken(token);
		
		if(user.getId() != userId) {
			throw new UnauthorizedRequestException("Unauthorized to update other users");
		}
		
		User userFromDb = userRepository.findById(userId).get();
		userFromDb.setName(request.getName());
//		userFromDb.setEmail(user.getEmail());
//		userFromDb.setPassword(passwordEncoder().encode(request.getPassword()));
		userFromDb.setCompany(request.getCompany());
		userFromDb.setSkills(request.getSkills());
		
		User updatedUser = userRepository.save(userFromDb);
		
		UserDto res = userToDto(updatedUser);
		return res;
	}
}
