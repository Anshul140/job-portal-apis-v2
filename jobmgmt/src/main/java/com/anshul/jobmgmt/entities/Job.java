package com.anshul.jobmgmt.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@Table(name = "jobs")
public class Job {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty(message = "Title field can not be empty! ")
	@Length(min = 5, max = 100, message = "Title must be between 5 to 100 characters! ")
	private String title;
	
	@NotEmpty(message = "CTC field cannot be empty! ")
	@Length(min = 4, max = 40, message = "CTC description must be between 4 to 40 characters! ")
	private String ctc;
	
	private JobStatus status;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	@NotEmpty(message = "Location field can not be empty! ")
	private String location;

	@ManyToOne
	@JoinColumn(name = "recruiter_id")
	private User user;
	
	@OneToMany(mappedBy = "job")
	private List<Application> applications = new ArrayList<>();
	
//	@NotEmpty(message = "Skill Requirements field cannot be empty! ")
	@ManyToMany
	@JoinTable(
			name="jobs_skills",
			joinColumns = @JoinColumn(name = "job_id"),
			inverseJoinColumns = @JoinColumn(name = "skill_id")
	)
	private List<Skill> requiredSkills = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCtc() {
		return ctc;
	}

	public void setCtc(String ctc) {
		this.ctc = ctc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void addApplication(Application application) {
		this.applications.add(application);
	}

	public Job(@Length(min = 5, max = 100) String title, @Length(min = 4, max = 40) String ctc, String location) {
		super();
		this.title = title;
		this.ctc = ctc;
		this.location = location;
	}

	public Job() {
		super();
		this.status = JobStatus.ACTIVE;
		// TODO Auto-generated constructor stub
	}
	
	
}
