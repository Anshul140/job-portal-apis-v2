package com.anshul.jobmgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.anshul.jobmgmt.entities.Role;
import com.anshul.jobmgmt.repositories.RoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

	@Autowired private RoleRepository roleRepo;
	
	@Test
	public void testCreateRoles() {
		Role admin = new Role("ROLE_ADMIN");
		Role recruiter = new Role("ROLE_RECRUITER");
		Role candidate = new Role("ROLE_CANDIDATE");
		
	    roleRepo.saveAll(List.of(admin, recruiter, candidate));
	    
	    long numberOfRoles = roleRepo.count();
	    
	    assertEquals(3, numberOfRoles);
	}
	
	@Test
	public void testListRoles() {
		List<Role> listRoles = roleRepo.findAll();
		assertThat(listRoles.size()).isGreaterThan(0);
		
		listRoles.forEach(System.out::println);
	}
	
}
