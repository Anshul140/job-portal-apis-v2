package com.anshul.jobmgmt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.jobmgmt.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

	Role findByName(String string);
}
