package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.RefreshToken;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.exception.InvalidRefreshTokenException;
import com.aathi.authenticationsystem.exception.ResourceAccessDeniedException;
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
    public String createRefreshToken(User user){

        String token = jwtService.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));

        if(refreshToken.isRevoked()){
            throw new InvalidRefreshTokenException("Invalid Refresh Token");
        }

        if(refreshToken.isExpired()){
            throw new InvalidRefreshTokenException("Invalid Refresh Token");
        }

        return refreshToken;
    }

    @Transactional
    public String rotateRefreshToken(User user, RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        return createRefreshToken(user);
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
