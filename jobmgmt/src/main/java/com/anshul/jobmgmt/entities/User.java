package com.anshul.jobmgmt.entities;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length = 50)
	@Size(min = 3, max = 50, message = "Username must be min of 3 characters")
	private String name;
	
	public User(String name, String email, String password, String contactNumber, String company) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.contactNumber = contactNumber;
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false, unique = true, length = 50)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String password;
	
	@Column(nullable = false, length = 10)
	private String contactNumber;
	
	@Column(nullable = false, length = 20)
	private String company;
	
	@OneToOne
	@JoinColumn(name = "resume_id")
	private Resume resume;
	
	@ManyToMany
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private List<Role> roles = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			name="users_skills",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "skill_id")
	)
	private List<Skill> skills = new ArrayList<>();
	
	@OneToMany(mappedBy = "user" ,fetch = FetchType.LAZY)
	private List<Job> jobs = new ArrayList<>();
	
	@OneToMany(
			mappedBy = "candidate",
			fetch = FetchType.LAZY, 
			orphanRemoval = true
	) @JsonIgnoreProperties("candidate")
	private List<Application> applications;
	
	public void addSkill(Skill s) {
		this.skills.add(s);
	}
	
	public void addJobs(Job job) {
		this.jobs.add(job);
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
	}
	
	public void addApplication(Application application) {
		this.applications.add(application);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for(Role role: roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
