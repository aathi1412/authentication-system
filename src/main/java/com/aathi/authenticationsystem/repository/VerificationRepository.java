package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {
}
