package com.aathi.authenticationsystem.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

        private final String KEY = "jf1^/,+^J+Y(/cN4BU;SBYmjHtr>;/ZRt[u>6v36.;[";
        private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(KEY.getBytes());
        private final Duration EXPIRATION = Duration.ofMinutes(15);

    public String generateAccessToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(EXPIRATION)))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
