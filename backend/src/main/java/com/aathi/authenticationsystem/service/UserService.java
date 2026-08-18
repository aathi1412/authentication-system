package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.user.UpdateUserRequest;
import com.aathi.authenticationsystem.dto.user.UserResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        return mapToUserResponse(savedUser);
    }

    @Transactional
    public ApiResponse changePassword(String oldPassword, String newPassword, CustomUserDetails userDetails) {

        User user = getUserById(userDetails.getId());

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            log.info("Invalid Password for {}", user.getEmail());
            throw new InvalidCredentialsException("Invalid Password!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("new password encoded and saved for {}", user.getEmail());

        log.info("Password Changed Successfully for {}", user.getEmail());

        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Changed Successfully!")
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phone(user.getPhone())
                .bio(user.getBio())
                .build();
    }

    public void updateProfileImage(MultipartFile image) {

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
                    }

                });
    }

    @Transactional
    public void resetFailedLoginAttempt(String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> user.setFailedAttempts(0));
    }



}
