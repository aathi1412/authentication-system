package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.response.AccessTokenResponse;
import com.aathi.authenticationsystem.dto.response.LoginResponse;
import com.aathi.authenticationsystem.dto.request.LoginRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.security.CookieService;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import com.aathi.authenticationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.aathi.authenticationsystem.constants.CookieConstants.REFRESH_TOKEN_COOKIE;

//import static com.aathi.authenticationsystem.security.CookieService.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtProperties jwtProperties;

    private final CookieService cookieService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerRequest(@Valid @RequestBody RegisterRequest request){
        RegisterResponse response = authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginRequest(@Valid @RequestBody LoginRequest request){
        LoginResult result = authenticationService.login(request);

        ResponseCookie cookie = cookieService.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.loginResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshTokenRequest(@CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken){
        RefreshResult result = authenticationService.refresh(refreshToken);

        ResponseCookie cookie = cookieService.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.accessToken());

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken){
        Long userId = userDetails.getId();
        authenticationService.logout(userId, refreshToken);

        ResponseCookie cookie = cookieService.clearRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
