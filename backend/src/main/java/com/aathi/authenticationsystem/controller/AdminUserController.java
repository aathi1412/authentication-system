package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.admin.AdminStatsResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserPageResponse;
import com.aathi.authenticationsystem.dto.admin.AdminUserResponse;
import com.aathi.authenticationsystem.dto.admin.ChangeRoleRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import com.aathi.authenticationsystem.service.AdminUserService;
import com.aathi.authenticationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthenticationService authenticationService;
    private final AdminUserService adminUserService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('READ_USERS')")
    public ResponseEntity<AdminUserPageResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean accountLocked,
            @RequestParam(required = false) Boolean enabled
    ) {
        return ResponseEntity.ok(adminUserService.getUsers(page, pageSize, search, role, accountLocked, enabled));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('READ_USERS')")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    @PutMapping("/users/{id}/lock")
    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    public ResponseEntity<AdminUserResponse> lockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails adminUser) {
        return ResponseEntity.ok(adminUserService.lockUser(id, adminUser.getId()));
    }

    @PutMapping("/users/{id}/unlock")
    @PreAuthorize("hasAuthority('UNLOCK_USERS')")
    public ResponseEntity<AdminUserResponse> unlockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.unlockUser(id));
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ADD_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminUserResponse> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails adminUser) {
        return ResponseEntity.ok(adminUserService.changeUserRole(id, request.getRole(), adminUser.getId()));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('DELETE_USERS') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> disableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails adminUser) {
        adminUserService.disableOrDeleteUser(id, adminUser.getId());
        return ResponseEntity.ok(ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("User has been disabled and all active sessions were revoked.")
                .build());
    }

    @PostMapping("/users/{id}/revoke-sessions")
    @PreAuthorize("hasAuthority('REVOKE_USER_SESSIONS')")
    public ResponseEntity<ApiResponse> revokeUserSessions(@PathVariable Long id) {
        int count = adminUserService.revokeUserSessions(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Successfully revoked " + count + " active session(s).")
                .build());
    }

    @PostMapping("/users/register")
    @PreAuthorize("hasAuthority('ADD_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<RegisterResponse> addAdmin(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authenticationService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('READ_USERS')")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminUserService.getStats());
    }

    @GetMapping("/home")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String getHomePage() {
        return "Welcome Admin Login Successful!";
    }
}
