
package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.request.ChangePasswordRequest;
import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.user.*;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import com.aathi.authenticationsystem.service.ActivityLogsService;
import com.aathi.authenticationsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ActivityLogsService activityLogsService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getUserResponse(userDetails));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @Valid @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateUser(userDetails, request));
    }

    @GetMapping("/security")
    public ResponseEntity<SecurityResponse> getSecurityDetails(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity
                .ok(userService.getSecurityDetails(userDetails));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ApiResponse response = userService.changePassword(request.currentPassword(), request.newPassword(), userDetails);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/profile/image")
    public ResponseEntity<?> updateProfileImage(@RequestParam("image")MultipartFile image){
        userService.updateProfileImage(image);
        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/activity")
    public PageResponse<ActivityLogResponse> getActivityLogs(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(required = false) String category,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        return activityLogsService.getActivities(
                customUserDetails.getId(),
                page,
                pageSize,
                search,
                category);
    }
}