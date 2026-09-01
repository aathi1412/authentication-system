package com.aathi.authenticationsystem.dto.user;

import java.util.List;

public record ActivityPageResponse<T>(
        List<T> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}