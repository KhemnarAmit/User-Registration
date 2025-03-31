package com.login.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.login.model.*;

public interface PaymentRepo extends JpaRepository<Payment,Long> {
	
	Optional<Payment> findByEmail(String email);
	Payment findByTransactionID(long TransactionID);
}
