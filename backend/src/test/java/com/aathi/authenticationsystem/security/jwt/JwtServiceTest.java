package com.aathi.authenticationsystem.security.jwt;

import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // 256-bit test key
        String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        JwtProperties properties = new JwtProperties(secretKey, Duration.ofMinutes(15), Duration.ofDays(7));
        jwtService = new JwtService(properties);
    }

    @Test
    void generateAccessToken_and_extractEmail_success() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .role(Role.USER)
                .build();

        String token = jwtService.generateAccessToken(user);
        assertThat(token).isNotBlank();

        String extractedEmail = jwtService.extractEmail(token);
        assertThat(extractedEmail).isEqualTo("test@example.com");
    }

    @Test
    void validateToken_matchingUser_returnsTrue() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .role(Role.USER)
                .build();

        String token = jwtService.generateAccessToken(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }

    @Test
    void validateToken_mismatchedUser_returnsFalse() {
        User user1 = User.builder().id(1L).email("user1@example.com").role(Role.USER).build();
        User user2 = User.builder().id(2L).email("user2@example.com").role(Role.USER).build();

        String token = jwtService.generateAccessToken(user1);
        CustomUserDetails userDetails = new CustomUserDetails(user2);

        assertThat(jwtService.validateToken(token, userDetails)).isFalse();
    }

    @Test
    void generateRefreshToken_returnsSecureRandomString() {
        String token1 = jwtService.generateRefreshToken();
        String token2 = jwtService.generateRefreshToken();

        assertThat(token1).isNotBlank();
        assertThat(token2).isNotBlank();
        assertThat(token1).isNotEqualTo(token2);
    }
}
