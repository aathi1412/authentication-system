package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.admin.AdminStatsResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserResponse;
import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.exception.BadRequestException;
import com.aathi.authenticationsystem.exception.UserNotFoundException;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.RefreshTokenRepository;
import com.aathi.authenticationsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ActivityLogsService activityLogsService;

    @InjectMocks
    private AdminUserService adminUserService;

    private User targetUser;
    private User superAdmin;

    @BeforeEach
    void setUp() {
        targetUser = User.builder()
                .id(2L)
                .email("user@example.com")
                .name("Target User")
                .role(Role.USER)
                .enabled(true)
                .accountLocked(false)
                .failedAttempts(0)
                .build();

        superAdmin = User.builder()
                .id(1L)
                .email("admin@example.com")
                .name("Super Admin")
                .role(Role.SUPER_ADMIN)
                .enabled(true)
                .accountLocked(false)
                .build();
    }

    @Test
    void lockUser_locksTargetUserAndRevokesTokens() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse response = adminUserService.lockUser(2L, 1L);

        assertThat(response.accountLocked()).isTrue();
        assertThat(targetUser.isAccountLocked()).isTrue();
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(2L);
    }

    @Test
    void lockUser_selfLock_throwsBadRequestException() {
        assertThatThrownBy(() -> adminUserService.lockUser(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot lock your own account");
    }

    @Test
    void lockUser_lastSuperAdmin_throwsBadRequestException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(userRepository.countByRole(Role.SUPER_ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> adminUserService.lockUser(1L, 2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot lock the last remaining super-admin account");
    }

    @Test
    void unlockUser_resetsLockAndFailedAttempts() {
        targetUser.setAccountLocked(true);
        targetUser.setFailedAttempts(5);
        targetUser.setLockTime(Instant.now());

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse response = adminUserService.unlockUser(2L);

        assertThat(response.accountLocked()).isFalse();
        assertThat(targetUser.isAccountLocked()).isFalse();
        assertThat(targetUser.getFailedAttempts()).isEqualTo(0);
        assertThat(targetUser.getLockTime()).isNull();
    }

    @Test
    void changeUserRole_updatesRoleSuccessfully() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse response = adminUserService.changeUserRole(2L, Role.ADMIN, 1L);

        assertThat(response.role()).isEqualTo(Role.ADMIN);
        assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void changeUserRole_demoteLastSuperAdmin_throwsBadRequestException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(userRepository.countByRole(Role.SUPER_ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> adminUserService.changeUserRole(1L, Role.USER, 2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot demote the last remaining super-admin account");
    }

    @Test
    void revokeUserSessions_callsRepository() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        adminUserService.revokeUserSessions(2L);

        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(2L);
    }

    @Test
    void disableOrDeleteUser_disablesUserAndRevokesTokens() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        adminUserService.disableOrDeleteUser(2L, 1L);

        assertThat(targetUser.isEnabled()).isFalse();
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(2L);
    }

    @Test
    void getStats_aggregatesCounts() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByEnabledTrue()).thenReturn(85L);
        when(userRepository.countByAccountLockedTrue()).thenReturn(3L);
        when(refreshTokenRepository.countActiveSessions(any(Instant.class))).thenReturn(25L);
        when(userRepository.countByCreatedAtAfter(any(Instant.class))).thenReturn(7L);

        AdminStatsResponse stats = adminUserService.getStats();

        assertThat(stats.totalUsers()).isEqualTo(100L);
        assertThat(stats.verifiedUsers()).isEqualTo(85L);
        assertThat(stats.lockedAccounts()).isEqualTo(3L);
        assertThat(stats.activeSessions()).isEqualTo(25L);
        assertThat(stats.recentRegistrations()).isEqualTo(7L);
    }
}
