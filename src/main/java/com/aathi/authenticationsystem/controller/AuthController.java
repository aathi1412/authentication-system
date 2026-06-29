package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.DTO.AuthResponse;
import com.aathi.authenticationsystem.DTO.LoginRequest;
import com.aathi.authenticationsystem.DTO.RegisterRequest;
import com.aathi.authenticationsystem.DTO.RegisterResponse;
import com.aathi.authenticationsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerRequest(@Valid @RequestBody RegisterRequest request){
        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginRequest(@RequestBody LoginRequest request){
        AuthResponse authResponse = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponse);
    }
}
