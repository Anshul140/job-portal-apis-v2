package com.anshul.jobmgmt;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.anshul.jobmgmt.jwt.JwtTokenFilter;
import com.anshul.jobmgmt.repositories.UserRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
		prePostEnabled = false,
		securedEnabled = false,
		jsr250Enabled = true
)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired private UserRepository userRepository;
	@Autowired private JwtTokenFilter jwtTokenFilter;
	@Autowired private MyCorsFilter corsFilter;
	
	/*
	@Bean
	public FilterRegistrationBean corsFilter() {
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedOriginPattern("Authorization");
		config.addAllowedOriginPattern("Content-Type");
		config.addAllowedOriginPattern("Accept");
		config.addAllowedOriginPattern("POST");
		config.addAllowedOriginPattern("GET");
		config.addAllowedOriginPattern("DELETE");
		config.addAllowedOriginPattern("UPDATE");
		config.addAllowedOriginPattern("OPTIONS");
		config.setMaxAge(3600L);
		
		source.registerCorsConfiguration("/**", config);
		
		FilterRegistrationBean bean = new FilterRegistrationBean(
				new CorsFilter(source)
		);
		
		return bean;
	}
	*/
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username -> userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User "+username+" not found!"))
		);
	}


	@Override @Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.exceptionHandling().authenticationEntryPoint(
				(request, response, ex) -> {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
							ex.getMessage());
				}
		);
		
		http.authorizeRequests()
		    .antMatchers("/auth/login").permitAll()
		    .antMatchers("/auth/signup").permitAll()
		    .antMatchers("/skills/getAllSkills").permitAll()
		    .anyRequest().authenticated();
		
		http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	
}
