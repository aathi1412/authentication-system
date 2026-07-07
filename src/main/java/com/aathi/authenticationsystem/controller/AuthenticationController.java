package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.request.*;
import com.aathi.authenticationsystem.dto.response.*;
import com.aathi.authenticationsystem.security.cookies.CookieService;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerRequest(@Valid @RequestBody RegisterRequest request){
        RegisterResponse response = authenticationService.registerUser(request);

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

    @GetMapping("/verify")
    public ResponseEntity<ErrorResponse> verifyEmail(@Valid @RequestParam String token){
        ErrorResponse response = authenticationService.verifyEmail(token);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ErrorResponse> forgetPassword(@Valid @RequestBody ForgotPasswordRequest request){
        ErrorResponse Response = authenticationService.forgotPassword(request.email());

        return ResponseEntity
                .ok()
                .body(Response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ErrorResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        ErrorResponse Response = authenticationService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity
                .ok()
                .body(Response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ErrorResponse> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ErrorResponse response = authenticationService.changePassword(request.oldPassword(), request.newPassword(), userDetails);

        return ResponseEntity
                .ok()
                .body(response);
    }
}
