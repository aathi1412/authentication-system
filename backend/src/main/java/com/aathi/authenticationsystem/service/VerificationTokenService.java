package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.exception.InvalidVerificationTokenException;
import com.aathi.authenticationsystem.exception.VerificationTokenExpiredException;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.models.VerificationToken;
import com.aathi.authenticationsystem.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    private final Logger log = LoggerFactory.getLogger(VerificationTokenService.class);
    @Transactional
    public void generateAndSendVerificationEmail(User user){

        String verificationToken = UUID.randomUUID().toString();
        log.info("generated verification token for user {}", user.getEmail());

        VerificationToken token = VerificationToken.builder()
                .token(verificationToken)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .user(user)
                .build();

        verificationTokenRepository.save(token);
        log.info("saved token for user {}", user.getEmail());

        emailService.sendVerificationEmail(user, verificationToken);
        log.info("Email sent for user {} successfully", user.getEmail());

    }
    public VerificationToken verifyToken(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("invalid Verification token"));

        if (verificationToken.isExpired()){
            throw new VerificationTokenExpiredException("Verification Token has Expired, please send a new Verification Email.");
        }

        return verificationToken;
    }

    public void deleteVerficationToken(VerificationToken verificationToken){
        verificationTokenRepository.delete(verificationToken);
    }
    @Transactional
    public void resendVerificationEmail(User user){

        verificationTokenRepository.findByUser(user)
                .ifPresent(verificationTokenRepository::delete);

        log.info("calling generateAndSendVerificationEmail for user {}", user.getEmail());
        generateAndSendVerificationEmail(user);

    }
}
