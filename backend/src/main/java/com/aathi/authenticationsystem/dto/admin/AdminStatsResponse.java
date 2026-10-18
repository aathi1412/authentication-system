package com.aathi.authenticationsystem.dto.admin;

import lombok.Builder;

@Builder
public record AdminStatsResponse(
        long totalUsers,
        long verifiedUsers,
        long lockedAccounts,
        long activeSessions,
        long recentRegistrations
) {}
