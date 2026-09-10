package com.aathi.authenticationsystem.dto.admin;

import com.aathi.authenticationsystem.enums.Role;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String bio,
        String profileImage,
        Role role,
        boolean enabled,
        boolean accountLocked,
        int failedAttempts,
        Instant lockTime,
        Instant lastLogin,
        Instant createdAt,
        Instant updatedAt
) {
}