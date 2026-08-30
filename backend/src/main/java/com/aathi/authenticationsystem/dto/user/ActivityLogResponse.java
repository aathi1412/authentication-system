package com.aathi.authenticationsystem.dto.user;

import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.enums.ActivityType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ActivityLogResponse(
        ActivityType type,
        String title,
        String description,
        ActivityCategory category,
        Instant ActivityTime
) {}
