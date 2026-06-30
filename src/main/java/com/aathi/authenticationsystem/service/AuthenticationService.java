package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.request.LoginRequest;
import com.aathi.authenticationsystem.dto.request.RefreshTokenRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.LoginResponse;
import com.aathi.authenticationsystem.dto.response.TokenResponse;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.dto.response.UserResponse;
import com.aathi.authenticationsystem.entity.RefreshToken;
import com.aathi.authenticationsystem.entity.Role;
import com.aathi.authenticationsystem.entity.User;
import com.aathi.authenticationsystem.exception.AccountNotVerifiedException;
import com.aathi.authenticationsystem.exception.EmailAlreadyExistsException;
import com.aathi.authenticationsystem.exception.InvalidCredentialsException;
import com.aathi.authenticationsystem.repository.UserRepository;
import com.aathi.authenticationsystem.security.jwt.JwtService;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
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

    public LoginResponse login(LoginRequest request){
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException ex){
            throw new AccountNotVerifiedException("Account Not Verified, Please Verify with Email.");
        } catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        if(customUserDetails == null){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        String accessToken = jwtService.generateAccessToken(customUserDetails.getUser());
        String refreshToken = refreshTokenService.createRefreshToken(customUserDetails.getUser());

        return LoginResponse.builder()
                .tokenResponse(TokenResponse.builder()
                        .message("Login Successful")
                        .tokenType("Bearer")
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(900)
                        .build()
                )
                .userResponse(UserResponse.builder()
                        .id(customUserDetails.getId())
                        .name(customUserDetails.getName())
                        .email(customUserDetails.getUsername())
                        .role(customUserDetails.getRole())
                        .build()
                )
                .build();

    }

    public void refresh(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);



    }
}
