package com.anshul.jobmgmt.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.anshul.jobmgmt.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired UserRepository userRepo;
	
	@Test
	public void testCreateUser() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "Rohan128";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		User newUser = new User("Rohan Gupta","rohan@gmail.com", encodedPassword, "7808477163", "Quantiphi");
		
		User savedUser = userRepo.save(newUser);
		
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testAssignRolesToUser() {
		Integer userId = 3;
		
		User user = userRepo.findById(userId).get();
		user.addRole(new Role(2));
		
		User updatedUser = userRepo.save(user);
		
		assertThat(updatedUser.getRoles()).hasSize(1);
	}
}
