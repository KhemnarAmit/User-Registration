package com.login.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.login.model.*;

public interface ContactRepo extends JpaRepository<Contact,Long> {
	
	Optional<Contact> findByEmail(String Email);
}
