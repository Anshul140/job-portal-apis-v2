package com.anshul.jobmgmt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.jobmgmt.entities.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long>{

}
