package com.anshul.jobmgmt.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.anshul.jobmgmt.entities.Skill;
import com.anshul.jobmgmt.entities.User;

public interface SkillRepository extends JpaRepository<Skill, Integer>{

}
