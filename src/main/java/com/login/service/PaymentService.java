package com.login.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.model.Payment;
import com.login.repo.PaymentRepo;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentRepo paymentRepo;
	
	public boolean registerPaymentDetails(Payment payment) {
		
		Optional<Payment> user = paymentRepo.findByEmail(payment.getEmail());
		
		if(user.isPresent()) {
			return false;
		}
		paymentRepo.save(payment);
		return true;
	}
	
	public Payment getPaymentByTransactionID(long transactionID) {
		
		Payment payment = paymentRepo.findByTransactionID(transactionID);
		return payment;
		
	}
	
}
