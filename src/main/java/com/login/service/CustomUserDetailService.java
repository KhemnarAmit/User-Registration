package com.login.service;

import java.util.Optional;
import com.login.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.login.repo.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	public UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> user = userRepository.findByUsername(username);
		
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("User Not Found");
		}
		
		User foundUser = user.get();
		
		return org.springframework.security.core.userdetails.User
				.withUsername(foundUser.getUsername())
				.password(foundUser.getPassword())
				.roles("USER")
				.build();
	}
	
	
}
