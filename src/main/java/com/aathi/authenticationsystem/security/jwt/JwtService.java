package com.aathi.authenticationsystem.security.jwt;

import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.entity.User;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
//@RequiredArgsConstructor
public class JwtService {

//    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final Duration expiration;

    public JwtService(JwtProperties jwtProperties) {
        secretKey = Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
        expiration = jwtProperties.accessTokenExpiration();
    }


    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expiration)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public String extractEmail(String token){

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, CustomUserDetails customUserDetails){
        return extractEmail(token).equals(customUserDetails.getUsername());
    }
}
