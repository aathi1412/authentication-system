package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.models.VerificationToken;
import com.aathi.authenticationsystem.exception.InvalidVerificationTokenException;
import com.aathi.authenticationsystem.exception.VerificationTokenExpiredException;
import com.aathi.authenticationsystem.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public String generateVerificationToken(User user){
        String verificationToken = UUID.randomUUID().toString();

        VerificationToken token = VerificationToken.builder()
                .token(verificationToken)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .user(user)
                .build();

        verificationTokenRepository.save(token);

        return verificationToken;
    }

    public User verifyToken(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("invalid Verification token"));

        if (verificationToken.isExpired()){
            throw new VerificationTokenExpiredException("Verification Token has Expired, please send a new Verification Email.");
        }
        return verificationToken.getUser();
    }
}
