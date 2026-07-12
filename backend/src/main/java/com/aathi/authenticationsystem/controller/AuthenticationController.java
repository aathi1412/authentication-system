package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.request.*;
import com.aathi.authenticationsystem.dto.response.AccessTokenResponse;
import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.response.LoginResponse;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.enums.VerificationStatus;
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
import org.springframework.web.servlet.view.RedirectView;

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

    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@Valid @RequestParam String token){
        VerificationStatus status = authenticationService.verifyEmail(token);

        return new RedirectView("http://localhost:5173/email-verification?status=" +
                status.name().toLowerCase());


    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse> resendVerifyEmail(@Valid @RequestBody EmailVerificationRequest request){
        ApiResponse response = authenticationService.resendVerificationEmail(request.email().toLowerCase());

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgetPassword(@Valid @RequestBody ForgotPasswordRequest request){
        ApiResponse Response = authenticationService.forgotPassword(request.email());

        return ResponseEntity
                .ok()
                .body(Response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        ApiResponse Response = authenticationService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity
                .ok()
                .body(Response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ApiResponse response = authenticationService.changePassword(request.oldPassword(), request.newPassword(), userDetails);

        return ResponseEntity
                .ok()
                .body(response);
    }
}
