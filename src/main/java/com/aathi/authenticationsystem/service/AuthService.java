package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.DTO.*;
import com.aathi.authenticationsystem.entity.Role;
import com.aathi.authenticationsystem.entity.User;
import com.aathi.authenticationsystem.exception.EmailAlreadyExistsException;
import com.aathi.authenticationsystem.exception.InvalidCredentialsException;
import com.aathi.authenticationsystem.repository.UserRepository;
import com.aathi.authenticationsystem.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public RegisterResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already Exists, try different email");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        UserResponse response = UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();

        return RegisterResponse.builder()
                .message("Registration Successful")
                .userResponse(response)
                .build();
    }

    public AuthResponse login(LoginRequest request){
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        if(user == null){
            System.out.println("not found");
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .message("Login Successful")
                .tokenType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900)
                .userResponse(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getUsername())
                        .role(user.getRole())
                        .build())
                .build();

    }
}
