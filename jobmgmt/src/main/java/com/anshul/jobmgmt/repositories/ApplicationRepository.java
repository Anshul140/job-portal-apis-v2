package com.anshul.jobmgmt.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.anshul.jobmgmt.entities.Application;
import com.anshul.jobmgmt.entities.ApplicationStatus;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer>{

	// find all applications for a candidate
	@Query("SELECT a FROM Application a WHERE a.candidate.id = :userId")
	List<Application> findByUserId(@Param("userId") Integer userId);
	
	// find all applications for a recruiter
	@Query("SELECT a FROM Application a WHERE a.job.user.id = :userId")
	List<Application> findAllByRecruiterId(@Param("userId") Integer userId);
	
	@Query("SELECT a FROM Application a WHERE a.job.id = :jobId")
	List<Application> findByJobId(@Param("jobId") Integer jobId);
	
	// get all shortlisted applications of jobId
	@Query("SELECT a FROM Application a WHERE a.job.id = :jobId AND a.status = :status")
	List<Application> findByJobIdAndApplicationStatus(
			@Param("jobId") Integer jobId, 
			@Param("status") ApplicationStatus status
	);
}
