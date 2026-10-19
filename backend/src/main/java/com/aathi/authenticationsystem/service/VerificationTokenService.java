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
        VerificationToken token = verificationTokenRepository
                .findByUser(user)
                .orElseGet(() -> VerificationToken.builder().user(user).build());

        String verificationToken = UUID.randomUUID().toString();
        log.info("generated verification token for user {}", user.getEmail());

        token.setToken(verificationToken);
        token.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        token.setCreatedAt(Instant.now());
        token.setUser(user);

        verificationTokenRepository.save(token);
        log.info("saved token for user {}", user.getEmail());

        emailService.sendVerificationEmail(user, verificationToken);
        log.info("Email sent for user {} successfully", user.getEmail());
    }

    public VerificationToken verifyToken(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidOrExpiredVerificationTokenException("Invalid or expired verification token"));

        if (verificationToken.isExpired()) {
            throw new InvalidOrExpiredVerificationTokenException("Invalid or expired verification token");
        }

        return verificationToken;
    }

    public void deleteVerificationToken(VerificationToken verificationToken){
        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void resendVerificationEmail(User user){
        VerificationToken token = verificationTokenRepository
                .findByUser(user)
                .orElseGet(() -> VerificationToken.builder().user(user).build());

        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        token.setCreatedAt(Instant.now());
        token.setUser(user);

        verificationTokenRepository.save(token);
        emailService.sendVerificationEmail(user, token.getToken());
        log.info("Email resend for user {}", user.getEmail());
    }
}
