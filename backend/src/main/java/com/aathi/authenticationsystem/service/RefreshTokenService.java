package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.exception.InvalidRefreshTokenException;
import com.aathi.authenticationsystem.exception.ResourceAccessDeniedException;
import com.aathi.authenticationsystem.models.RefreshToken;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.RefreshTokenRepository;
import com.aathi.authenticationsystem.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user, boolean rememberMe, Instant sessionExpiryDate){

        Instant now = Instant.now();

        Instant expiryDate = rememberMe
                ? now.plus(7, ChronoUnit.DAYS)
                : now.plus(1, ChronoUnit.DAYS);

        if (expiryDate.isAfter(sessionExpiryDate)) {
            expiryDate = sessionExpiryDate;
        }

        String token = jwtService.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .createdAt(Instant.now())
                .sessionExpiryDate(sessionExpiryDate)
                .rememberMe(rememberMe)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));

        if(refreshToken.isRevoked() ||
                refreshToken.isExpired()){
            throw new InvalidRefreshTokenException("Invalid Refresh Token");
        }

        if (refreshToken.isSessionExpired()) {
            throw new InvalidRefreshTokenException("Session Expired");
        }

        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(User user, RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        return createRefreshToken(user, refreshToken.isRememberMe(), refreshToken.getSessionExpiryDate());
    }

    @Transactional
    public void revokeRefreshToken(Long authenticatedUserId, String refreshToken){

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));

        if(!token.getUser().getId().equals(authenticatedUserId)){
            throw new ResourceAccessDeniedException("You are not authorized to revoke this refresh token.");
        }

        if(token.isRevoked() || token.isExpired()){
            return;
        }

        token.setRevoked(true);
    }
}
