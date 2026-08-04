package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.exception.InvalidOrExpiredVerificationTokenException;
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

        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidOrExpiredVerificationTokenException("invalid or Expired Verification token"));
    }

    public void deleteVerificationToken(VerificationToken verificationToken){
        verificationTokenRepository.delete(verificationToken);
    }
    @Transactional
    public void resendVerificationEmail(User user){

        VerificationToken token = verificationTokenRepository
                .findByUser(user)
                .orElse(new VerificationToken());

        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        token.setCreatedAt(Instant.now());
        token.setUser(user);

        emailService.sendVerificationEmail(user, token.getToken());
        log.info("Email resend for user {}", user.getEmail());

    }
}
