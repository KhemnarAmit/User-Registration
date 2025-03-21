package com.login.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.login.model.*;
import com.login.repo.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
	public User registerUser(String name, String username, String password) {

		if(userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("user already exist");
		}
		
		User user = new User();
		
		user.setName(name);
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		
		return userRepository.save(user);
	}
}
