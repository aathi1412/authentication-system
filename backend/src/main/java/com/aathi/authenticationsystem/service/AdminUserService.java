package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.admin.AdminUserPageResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserResponse;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    public AdminUserPageResponse getUsers(int page, int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<AdminUserResponse> adminUserResponses = userRepository.findAll(pageable)
                .map(this::mapToAdminUserResponse);

        return mapToAdminUserPageResponse(adminUserResponses);
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
