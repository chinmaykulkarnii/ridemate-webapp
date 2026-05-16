package com.ridemate.repository;

import com.ridemate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByReferralCode(String referralCode);

    // Analytics queries
    long countByCreatedAtAfter(LocalDateTime date);
    long countByIsIdVerifiedTrue();
}