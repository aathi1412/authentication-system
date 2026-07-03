package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.entity.PasswordResetToken;
import com.aathi.authenticationsystem.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.request.LoginRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

import static com.aathi.authenticationsystem.constants.SecurityConstants.TOKEN_TYPE;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Registration Failed: email {} already exists", request.getEmail());
            throw new EmailAlreadyExistsException("Email already Exists, try different email");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User {} Saved Successfully", savedUser.getEmail());

        String verificationToken = verificationTokenService.generateVerificationToken(user);

        emailService.sendVerificationEmail(user, verificationToken);

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

    public LoginResult login(LoginRequest request){
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

        CustomUserDetails customUserDetails = (CustomUserDetails) Objects.requireNonNull(authentication.getPrincipal());

        String accessToken = jwtService.generateAccessToken(customUserDetails.getUser());
        String refreshToken = refreshTokenService.createRefreshToken(customUserDetails.getUser());

        return new LoginResult(
                LoginResponse.builder()
                        .accessTokenResponse(AccessTokenResponse.builder()
                                .message("Login Successful")
                                .tokenType(TOKEN_TYPE)
                                .accessToken(accessToken)
                                .expiresIn(jwtProperties.accessTokenExpiration().toSeconds())
                                .build()
                        )
                        .userResponse(UserResponse.builder()
                                .id(customUserDetails.getId())
                                .name(customUserDetails.getName())
                                .email(customUserDetails.getUsername())
                                .role(customUserDetails.getRole())
                                .build()
                        )
                        .build(),
                refreshToken  // refresh token
        );
    }

    public RefreshResult refresh(String token){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(token);

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);

        String newRefreshToken = refreshTokenService.rotateRefreshToken(user, refreshToken);

        return new RefreshResult(
                AccessTokenResponse.builder()
                        .message("new access token generated")
                        .tokenType(TOKEN_TYPE)
                        .accessToken(accessToken)
                        .expiresIn(jwtProperties.accessTokenExpiration().toSeconds())
                        .build(),
                newRefreshToken
        );
    }

    public void logout(Long userId, String refreshToken){

        refreshTokenService.revokeRefreshToken(userId,refreshToken);
    }

    @Transactional
    public ErrorResponse verifyEmail(String token){
        User user = verificationTokenService.verifyToken(token);

        user.setEnabled(true);

        return ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Email verified Successfully")
                .build();
    }

    public ErrorResponse forgotPassword(String email){
        userRepository.findByEmail(email).ifPresent( user -> {
            try {
                PasswordResetToken resetToken = passwordResetTokenService.createOrReplacePasswordResetToken(user);
                emailService.sentResetToken(user, resetToken.getToken());
            }catch (MailException ex) {
                log.error("can't send a mail {} error occurs", ex.getMessage());
            }
        });

        return ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("If an account with that email exists, a password reset link has been sent.")
                .build();
    }

    @Transactional
    public ErrorResponse resetPassword(String token, String newPassword){
        PasswordResetToken resetToken = passwordResetTokenService.verifyPasswordResetToken(token);

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        passwordResetTokenService.deletePasswordResetToken(resetToken);

        return ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Reset Successfully")
                .build();
    }

    @Transactional
    public ErrorResponse changePassword(String oldPassword, String newPassword, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getId())
                            .orElseThrow(() -> new UserNotFoundException("User Not Found!"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new InvalidCredentialsException("Invalid Password!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        return ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Changed Successfully!")
                .build();

    }
}
