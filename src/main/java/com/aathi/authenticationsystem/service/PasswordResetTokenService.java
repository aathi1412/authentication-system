package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.PasswordResetToken;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.exception.InvalidPasswordResetTokenException;
import com.aathi.authenticationsystem.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public PasswordResetToken createOrReplacePasswordResetToken(User user){

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user)
                .orElseGet(() -> PasswordResetToken.builder()
                        .createdAt(Instant.now())
                        .user(user)
                        .build());

        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiryDate(Instant.now().plus(15, ChronoUnit.MINUTES));

        passwordResetTokenRepository.save(passwordResetToken);

         return passwordResetToken;
    }

    public PasswordResetToken verifyPasswordResetToken(String token){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Invalid or expired reset token."));

        if(resetToken.isExpired()){
            throw new InvalidPasswordResetTokenException("Invalid or expired reset token.");
        }

        return resetToken;
    }

    @Transactional
    public void deletePasswordResetToken(PasswordResetToken token){
        passwordResetTokenRepository.delete(token);
    }
}
