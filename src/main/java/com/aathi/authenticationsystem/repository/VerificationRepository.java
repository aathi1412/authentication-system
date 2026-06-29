package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {
    Optional< VerificationToken> findByToken(String token);
}
