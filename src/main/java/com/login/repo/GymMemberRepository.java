package com.login.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.login.model.GymMember;

public interface GymMemberRepository extends JpaRepository<GymMember, Long> {
    boolean existsByEmail(String email);
}
