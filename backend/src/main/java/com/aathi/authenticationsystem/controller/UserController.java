
package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.user.UpdateUserRequest;
import com.aathi.authenticationsystem.dto.user.UserResponse;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
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

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getUserResponse(userDetails));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @Valid @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateUser(userDetails, request));
    }

    @PostMapping("/profile/image")
    public ResponseEntity<?> updateProfileImage(@RequestParam("image")MultipartFile image){
        userService.updateProfileImage(image);
        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/dashboard")
    public String getDashboard(){
        return "Dashboard!";
    }

    @GetMapping("/about")
    public String getAbout(){
        return "About!";
    }

    @GetMapping("/contact")
    public String getContact(){
        return "Contact!";
    }
}