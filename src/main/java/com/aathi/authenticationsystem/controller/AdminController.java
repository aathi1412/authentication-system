package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.service.AdminService;
import com.aathi.authenticationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authenticationService;
    private final AdminService adminService;

    @GetMapping("/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String getHomePage(){
        return "Welcome Admin Login Successful!";
    }

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> addAdmin(@Valid @RequestBody RegisterRequest request){
        RegisterResponse response = authenticationService.registerAdmin(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PutMapping("/update/user")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@RequestBody String name){
        return "update user" + name;
    }
}
