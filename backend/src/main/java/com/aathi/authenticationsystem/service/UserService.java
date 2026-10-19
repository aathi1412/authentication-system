package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.user.SecurityResponse;
import com.aathi.authenticationsystem.dto.user.UpdateUserRequest;
import com.aathi.authenticationsystem.dto.user.UserResponse;
import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.enums.ActivityType;
import com.aathi.authenticationsystem.exception.InvalidCredentialsException;
import com.aathi.authenticationsystem.exception.UserNotFoundException;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.UserRepository;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.aathi.authenticationsystem.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogsService activityLogsService;

    @Value("${app.upload.dir:./uploads/avatars}")
    private String uploadDir;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse getUserResponse(CustomUserDetails userDetails){
        User user = getUserByEmail(userDetails.getUsername());
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(CustomUserDetails userDetails, @Valid UpdateUserRequest request) {

        User savedUser = getUserByEmail(userDetails.getUsername());
        savedUser.setName(request.getName());
        savedUser.setPhone(request.getPhone());
        savedUser.setBio(request.getBio());

        activityLogsService.saveActivityLog(
                savedUser,
                "Profile updated",
                ActivityType.PROFILE_UPDATED,
                "Updated profile details",
                ActivityCategory.PROFILE
        );

        return mapToUserResponse(savedUser);
    }

    public SecurityResponse getSecurityDetails(CustomUserDetails userDetails) {
        User user = getUserByEmail(userDetails.getUsername());
        return mapToSecurityResponse(user);
    }

    @Transactional
    public ApiResponse changePassword(String currentPassword, String newPassword, CustomUserDetails userDetails) {

        User user = getUserById(userDetails.getId());

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            log.info("Invalid Password for {}", user.getEmail());
            activityLogsService.saveActivityLog(
                    user,
                    "Password Change Fail",
                    ActivityType.PASSWORD_CHANGED_FAILED,
                    "Password Changed Failed",
                    ActivityCategory.AUTHENTICATION
            );
            throw new InvalidCredentialsException("Invalid Password!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("new password encoded and saved for {}", user.getEmail());

        log.info("Password Changed Successfully for {}", user.getEmail());

        activityLogsService.saveActivityLog(
                user,
                "Password Changed",
                ActivityType.PASSWORD_CHANGED,
                "Password Changed Successfully",
                ActivityCategory.AUTHENTICATION
        );

        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Changed Successfully!")
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phone(user.getPhone())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .build();
    }

    public SecurityResponse mapToSecurityResponse(User user) {
        return SecurityResponse.builder()
                .emailVerified(user.isEnabled())
                .role(user.getRole().name())
                .accountLocked(user.isAccountLocked())
                .failedAttempts(user.getFailedAttempts())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Transactional
    public UserResponse updateProfileImage(CustomUserDetails userDetails, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Please select an image file to upload");
        }

        if (image.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Image size cannot exceed 5MB");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG, PNG, and WebP images are allowed");
        }

        String originalFilename = image.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            if (ext.equals(".png") || ext.equals(".webp") || ext.equals(".jpeg") || ext.equals(".jpg")) {
                extension = ext;
            }
        }

        String uniqueFilename = UUID.randomUUID() + extension;

        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            User user = getUserByEmail(userDetails.getUsername());

            // Delete previous uploaded avatar file if it was managed locally
            if (user.getProfileImage() != null && user.getProfileImage().contains("/profile/image/")) {
                String oldFilename = user.getProfileImage().substring(user.getProfileImage().lastIndexOf('/') + 1);
                Path oldFile = targetDir.resolve(oldFilename).normalize();
                if (oldFile.startsWith(targetDir) && Files.exists(oldFile)) {
                    Files.deleteIfExists(oldFile);
                }
            }

            Path targetFile = targetDir.resolve(uniqueFilename).normalize();
            if (!targetFile.startsWith(targetDir)) {
                throw new BadRequestException("Invalid file path");
            }

            Files.copy(image.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/api/users/profile/image/" + uniqueFilename;
            user.setProfileImage(imageUrl);
            User savedUser = userRepository.save(user);

            activityLogsService.saveActivityLog(
                    savedUser,
                    "Profile image updated",
                    ActivityType.PROFILE_UPDATED,
                    "Updated profile picture",
                    ActivityCategory.PROFILE
            );

            log.info("Profile image updated for user {}", user.getEmail());
            return mapToUserResponse(savedUser);
        } catch (IOException e) {
            log.error("Failed to store profile image for user {}: {}", userDetails.getUsername(), e.getMessage());
            throw new RuntimeException("Could not store profile image. Please try again.", e);
        }
    }

    public Resource loadProfileImage(String filename) {
        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = targetDir.resolve(filename).normalize();

            if (!filePath.startsWith(targetDir)) {
                throw new UserNotFoundException("Image not found");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new UserNotFoundException("Image not found");
            }
        } catch (MalformedURLException e) {
            throw new UserNotFoundException("Image not found");
        }
    }
    @Transactional
    public void lockOrUnlockAccount(String email){

        userRepository.findByEmail(email)
                .ifPresent(user -> {

                    if(!user.isAccountLocked()){
                        return;
                    }
                    Instant unlockTime = user.getLockTime().plus(15, ChronoUnit.MINUTES);

                    if(Instant.now().isAfter(unlockTime)){
                        user.setAccountLocked(false);
                        user.setFailedAttempts(0);
                        user.setLockTime(null);
                    }
                });

    }

    @Transactional
    public void increaseFailedLoginAttempt(String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedAttempts(user.getFailedAttempts() + 1);

                    if(user.getFailedAttempts() >= 5){
                        user.setAccountLocked(true);
                        user.setLockTime(Instant.now());

                        activityLogsService.saveActivityLog(
                                user,
                                "Account locked",
                                ActivityType.ACCOUNT_LOCKED,
                                "Too many failed attempts, account locked",
                                ActivityCategory.AUTHENTICATION
                        );
                    }

                });
    }

    @Transactional
    public void resetFailedLoginAttempt(String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> user.setFailedAttempts(0));
    }

}
