package com.aathi.authenticationsystem.dto.admin;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminUserPageResponse(
        List<AdminUserResponse> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {
}