package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.exception.InvalidOrExpiredVerificationTokenException;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.models.VerificationToken;
import com.aathi.authenticationsystem.repository.VerificationTokenRepository;
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
class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("verify@example.com")
                .name("Verify User")
                .enabled(false)
                .build();
    }

    @Test
    void generateAndSendVerificationEmail_createsTokenAndSendsEmail() {
        when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));

        verificationTokenService.generateAndSendVerificationEmail(testUser);

        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailService, times(1)).sendVerificationEmail(eq(testUser), anyString());
    }

    @Test
    void verifyToken_validToken_returnsVerificationToken() {
        VerificationToken validToken = VerificationToken.builder()
                .token("valid-uuid")
                .user(testUser)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        when(verificationTokenRepository.findByToken("valid-uuid")).thenReturn(Optional.of(validToken));

        VerificationToken result = verificationTokenService.verifyToken("valid-uuid");
        assertThat(result).isEqualTo(validToken);
    }

    @Test
    void verifyToken_expiredToken_throwsInvalidOrExpiredException() {
        VerificationToken expiredToken = VerificationToken.builder()
                .token("expired-uuid")
                .user(testUser)
                .expiryDate(Instant.now().minus(10, ChronoUnit.MINUTES))
                .build();

        when(verificationTokenRepository.findByToken("expired-uuid")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> verificationTokenService.verifyToken("expired-uuid"))
                .isInstanceOf(InvalidOrExpiredVerificationTokenException.class);
    }

    @Test
    void verifyToken_nonExistentToken_throwsInvalidOrExpiredException() {
        when(verificationTokenRepository.findByToken("not-found")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificationTokenService.verifyToken("not-found"))
                .isInstanceOf(InvalidOrExpiredVerificationTokenException.class);
    }
}
