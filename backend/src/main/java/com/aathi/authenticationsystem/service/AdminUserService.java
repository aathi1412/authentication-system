package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.admin.AdminStatsResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserPageResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserResponse;
import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.enums.ActivityType;
import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.exception.BadRequestException;
import com.aathi.authenticationsystem.exception.UserNotFoundException;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.RefreshTokenRepository;
import com.aathi.authenticationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ActivityLogsService activityLogsService;

    public AdminUserPageResponse getUsers(int page, int pageSize, String search, Role role, Boolean accountLocked, Boolean enabled){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());

        Page<AdminUserResponse> adminUserResponses = userRepository
                .findFilteredUsers(search, role, accountLocked, enabled, pageable)
                .map(this::mapToAdminUserResponse);

        return mapToAdminUserPageResponse(adminUserResponses);
    }

    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse lockUser(Long targetUserId, Long currentAdminId) {
        if (targetUserId.equals(currentAdminId)) {
            throw new BadRequestException("You cannot lock your own account.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        if (user.getRole() == Role.SUPER_ADMIN && userRepository.countByRole(Role.SUPER_ADMIN) <= 1) {
            throw new BadRequestException("Cannot lock the last remaining super-admin account.");
        }

        user.setAccountLocked(true);
        user.setLockTime(Instant.now());
        User savedUser = userRepository.save(user);

        // Revoke active sessions when locked
        refreshTokenRepository.revokeAllUserTokens(targetUserId);

        activityLogsService.saveActivityLog(
                savedUser,
                "Account administratively locked",
                ActivityType.ACCOUNT_LOCKED,
                "Account was locked by administrator (ID: " + currentAdminId + ")",
                ActivityCategory.AUTHENTICATION
        );

        log.info("User ID {} was locked by Admin ID {}", targetUserId, currentAdminId);
        return mapToAdminUserResponse(savedUser);
    }

    @Transactional
    public AdminUserResponse unlockUser(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        user.setAccountLocked(false);
        user.setLockTime(null);
        user.setFailedAttempts(0);
        User savedUser = userRepository.save(user);

        activityLogsService.saveActivityLog(
                savedUser,
                "Account administratively unlocked",
                ActivityType.ACCOUNT_CREATED,
                "Account was unlocked by administrator",
                ActivityCategory.AUTHENTICATION
        );

        log.info("User ID {} was unlocked", targetUserId);
        return mapToAdminUserResponse(savedUser);
    }

    @Transactional
    public AdminUserResponse changeUserRole(Long targetUserId, Role newRole, Long currentAdminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        if (user.getRole() == Role.SUPER_ADMIN && newRole != Role.SUPER_ADMIN) {
            long superAdminCount = userRepository.countByRole(Role.SUPER_ADMIN);
            if (superAdminCount <= 1) {
                throw new BadRequestException("Cannot demote the last remaining super-admin account.");
            }
        }

        Role oldRole = user.getRole();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        activityLogsService.saveActivityLog(
                savedUser,
                "Role changed",
                ActivityType.PROFILE_UPDATED,
                "Role changed from " + oldRole + " to " + newRole + " by admin ID " + currentAdminId,
                ActivityCategory.PROFILE
        );

        log.info("User ID {} role changed from {} to {} by Admin ID {}", targetUserId, oldRole, newRole, currentAdminId);
        return mapToAdminUserResponse(savedUser);
    }

    @Transactional
    public void disableOrDeleteUser(Long targetUserId, Long currentAdminId) {
        if (targetUserId.equals(currentAdminId)) {
            throw new BadRequestException("You cannot delete or disable your own account.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        if (user.getRole() == Role.SUPER_ADMIN && userRepository.countByRole(Role.SUPER_ADMIN) <= 1) {
            throw new BadRequestException("Cannot delete or disable the last remaining super-admin account.");
        }

        // Soft-disable user and revoke all refresh tokens to preserve audit logs
        user.setEnabled(false);
        user.setAccountLocked(true);
        userRepository.save(user);

        refreshTokenRepository.revokeAllUserTokens(targetUserId);

        activityLogsService.saveActivityLog(
                user,
                "Account disabled",
                ActivityType.ACCOUNT_LOCKED,
                "Account was disabled and sessions revoked by admin ID " + currentAdminId,
                ActivityCategory.AUTHENTICATION
        );

        log.info("User ID {} was disabled by Admin ID {}", targetUserId, currentAdminId);
    }

    @Transactional
    public int revokeUserSessions(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        int revokedCount = refreshTokenRepository.revokeAllUserTokens(targetUserId);

        activityLogsService.saveActivityLog(
                user,
                "All sessions revoked",
                ActivityType.LOGOUT,
                "All active sessions were revoked by administrator",
                ActivityCategory.AUTHENTICATION
        );

        log.info("Revoked {} sessions for user ID {}", revokedCount, targetUserId);
        return revokedCount;
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long verifiedUsers = userRepository.countByEnabledTrue();
        long lockedAccounts = userRepository.countByAccountLockedTrue();
        long activeSessions = refreshTokenRepository.countActiveSessions(Instant.now());
        long recentRegistrations = userRepository.countByCreatedAtAfter(Instant.now().minus(30, ChronoUnit.DAYS));

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .verifiedUsers(verifiedUsers)
                .lockedAccounts(lockedAccounts)
                .activeSessions(activeSessions)
                .recentRegistrations(recentRegistrations)
                .build();
    }

    public AdminUserPageResponse mapToAdminUserPageResponse(Page<AdminUserResponse> responses){
        return AdminUserPageResponse.builder()
                .content(responses.getContent())
                .page(responses.getNumber())
                .pageSize(responses.getSize())
                .totalElements(responses.getTotalElements())
                .totalPages(responses.getTotalPages())
                .build();
    }

    public AdminUserResponse mapToAdminUserResponse(User user){
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .failedAttempts(user.getFailedAttempts())
                .lockTime(user.getLockTime())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
