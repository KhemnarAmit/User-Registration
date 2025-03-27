package com.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.login.service.CustomUserDetailService;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailService userDetailService;
	
	public SecurityConfig(CustomUserDetailService userDetailService) {
		this.userDetailService = userDetailService;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
	                    .requestMatchers("/register", "/forgot-password","/verify","/verify1","/error","/success").permitAll() // Ensure "/verify" is allowed
	                    .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                    .loginPage("/login")
	                    .defaultSuccessUrl("/", true)
	                    .permitAll()
	            )
	            .logout(logout -> logout
	                    .logoutUrl("/logout")
	                    .logoutSuccessUrl("/login?logout")
	                    .permitAll()
	            )
	            .build();
	}

	
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
