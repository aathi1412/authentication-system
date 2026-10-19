package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.exception.InvalidRefreshTokenException;
import com.aathi.authenticationsystem.models.RefreshToken;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.RefreshTokenRepository;
import com.aathi.authenticationsystem.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();
    }

    @Test
    void createRefreshToken_persistsAndReturnsToken() {
        when(jwtService.generateRefreshToken()).thenReturn("random-refresh-token-string");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instant sessionExpiry = Instant.now().plus(30, ChronoUnit.DAYS);
        RefreshToken result = refreshTokenService.createRefreshToken(testUser, true, sessionExpiry);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("random-refresh-token-string");
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.isRevoked()).isFalse();
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void verifyRefreshToken_validToken_returnsRefreshToken() {
        RefreshToken validToken = RefreshToken.builder()
                .token("valid-token")
                .user(testUser)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .sessionExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

        RefreshToken verified = refreshTokenService.verifyRefreshToken("valid-token");
        assertThat(verified).isEqualTo(validToken);
    }

    @Test
    void verifyRefreshToken_revokedToken_throwsInvalidRefreshTokenException() {
        RefreshToken revokedToken = RefreshToken.builder()
                .token("revoked-token")
                .user(testUser)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .sessionExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> refreshTokenService.verifyRefreshToken("revoked-token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void verifyRefreshToken_expiredToken_throwsInvalidRefreshTokenException() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-token")
                .user(testUser)
                .expiryDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .sessionExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenService.verifyRefreshToken("expired-token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void verifyRefreshToken_notFound_throwsInvalidRefreshTokenException() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verifyRefreshToken("unknown"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rotateRefreshToken_revokesOldTokenAndCreatesNewOne() {
        RefreshToken oldToken = RefreshToken.builder()
                .token("old-token")
                .user(testUser)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .sessionExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .rememberMe(true)
                .revoked(false)
                .build();

        when(jwtService.generateRefreshToken()).thenReturn("new-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken rotated = refreshTokenService.rotateRefreshToken(testUser, oldToken);

        assertThat(oldToken.isRevoked()).isTrue();
        assertThat(rotated.getToken()).isEqualTo("new-token");
        assertThat(rotated.isRevoked()).isFalse();
    }
}
