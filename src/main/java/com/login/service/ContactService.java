package com.login.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.model.Contact;
import com.login.repo.*;

@Service
public class ContactService {
	
	@Autowired
	private ContactRepo contactRepo;
	
	public boolean registerContact(Contact contact) {
		
		Optional<Contact> user = contactRepo.findByEmail(contact.getEmail());
		
		if(user.isPresent()) {
			return false;
		}
		contactRepo.save(contact);
		return true;
	}
}
