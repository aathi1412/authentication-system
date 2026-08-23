package com.aathi.authenticationsystem.dto.user;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SecurityResponse(
        boolean emailVerified,
        String role,
        boolean accountLocked,
        int failedAttempts,
        Instant lastLogin,
        Instant createdAt
) {}
