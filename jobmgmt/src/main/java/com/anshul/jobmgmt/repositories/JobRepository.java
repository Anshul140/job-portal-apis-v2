package com.anshul.jobmgmt.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.anshul.jobmgmt.entities.Application;
import com.anshul.jobmgmt.entities.Job;
import com.anshul.jobmgmt.entities.JobStatus;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer>{

	@Query("SELECT j FROM Job j WHERE j.user.id = :userId AND j.status = :status")
	List<Job> findJobsByRecruiterId(
			@Param("userId") Integer userId,
			@Param("status") JobStatus status
	);
	
	
	List<Job> findByStatus(JobStatus status);
}
