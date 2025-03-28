package com.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.login.model.Contact;
import com.login.model.GymMember;
import com.login.repo.GymMemberRepository;
import com.login.repo.UserRepository;

@Service
public class GymMemberService {

    @Autowired
    private GymMemberRepository repository;

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerMember(GymMember member) {
        if (repository.existsByEmail(member.getEmail())) {
            return false; // Email already exists
        }
        member.setPassword(passwordEncoder.encode(member.getPassword())); // Encrypt password
        repository.save(member);
        return true;
    }
    
}
