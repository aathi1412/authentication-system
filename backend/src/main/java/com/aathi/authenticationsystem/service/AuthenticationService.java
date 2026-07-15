package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.request.LoginRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.*;
import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.enums.VerificationStatus;
import com.aathi.authenticationsystem.exception.*;
import com.aathi.authenticationsystem.models.PasswordResetToken;
import com.aathi.authenticationsystem.models.RefreshToken;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.models.VerificationToken;
import com.aathi.authenticationsystem.repository.UserRepository;
import com.aathi.authenticationsystem.security.jwt.JwtService;
import com.aathi.authenticationsystem.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final EmailValidationService emailValidationService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request){
        return register(request, Role.USER);
    }

    @Transactional
    public RegisterResponse registerAdmin(RegisterRequest request){
        return register(request, Role.ADMIN);
    }

    public RegisterResponse register(RegisterRequest request, Role role){

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Registration Failed: email {} already exists", request.getEmail());
            throw new EmailAlreadyExistsException("Email already Exists, try different email");
        }

        int domainStarts = request.getEmail().indexOf("@") + 1;
        String domain = request.getEmail().substring(domainStarts);

        log.info("Register Request: email {}, domain {}", request.getEmail(), domain);

        if(!emailValidationService.hasValidEmailDomain(domain)){
            log.warn("Registration Failed: invalid email domain {}", domain);
            throw new InvalidEmailDomainException("Please enter a valid email address.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User {} Saved Successfully", savedUser.getEmail());

        verificationTokenService.generateAndSendVerificationEmail(user);
        log.info("verification token generated ");
        log.info("verification Email sent to {}", user.getEmail());
        log.info("Registration Successful for user {}", request.getEmail());

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

        userService.lockOrUnlockAccount(request.getEmail());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException ex){
            log.error("Account Not Verified for user {}", request.getEmail());
            throw new AccountNotVerifiedException("Account Not Verified, Please Verify with Email.");
        } catch (BadCredentialsException ex){
            log.error("invalid Email or Password, error: {}", ex.getMessage());

            userService.increaseFailedLoginAttempt(request.getEmail());

            log.info("login attempt failed for user : {}", ex.getMessage());

            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) Objects.requireNonNull(authentication.getPrincipal());

        String accessToken = jwtService.generateAccessToken(customUserDetails.user());
        log.info("access token generated");

        String refreshToken = refreshTokenService.createRefreshToken(customUserDetails.user());
        log.info("refresh token generated");

        log.info("Login Successful for user {}", request.getEmail());

        userService.resetFailedLoginAttempt(request.getEmail());

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

        log.info("new access token generated for user {}", user.getEmail());
        log.info("new refresh token generated for user {}", user.getEmail());

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
        log.info("userId {} logout successfully", userId);
    }

    @Transactional
    public VerificationStatus verifyEmail(String token){
        log.info("Verifying email address for user {}", token);

        try{
            VerificationToken verificationToken = verificationTokenService.verifyToken(token);
            User  user = verificationToken.getUser();

            user.setEnabled(true);
            log.info("Email verified Successfully for user {}", user.getEmail());

            verificationTokenService.deleteVerificationToken(verificationToken);

            return VerificationStatus.VERIFIED;
        }
        catch (InvalidOrExpiredVerificationTokenException ex){
            log.error("{}", ex.getMessage());
            return VerificationStatus.INVALID_OR_EXPIRED;
        }
    }

    public ApiResponse resendVerificationEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User must be register before email verification"));

        if(user.isEnabled()){
            throw new BadRequestException("Email is already verified. Sign in to continue");
        }

        verificationTokenService.resendVerificationEmail(user);
        log.info("Email resend successfully for user {}", email);
        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message( "If an account exists and is not yet verified, a verification email has been sent.")
                .build();

    }

    public ApiResponse forgotPassword(String email){
        userRepository.findByEmail(email).ifPresent( user -> {
            try {
                PasswordResetToken resetToken = passwordResetTokenService.createOrReplacePasswordResetToken(user);
                emailService.sentResetToken(user, resetToken.getToken());
            }catch (MailException ex) {
                log.error("can't send a mail {} error occurs", ex.getMessage());
            }
        });

        log.info("password reset link has been sent to {}", email);
        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("If an account with that email exists, a password reset link has been sent.")
                .build();
    }

    @Transactional
    public ApiResponse resetPassword(String token, String newPassword){
        PasswordResetToken resetToken = passwordResetTokenService.verifyPasswordResetToken(token);

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("new password encoded for {}", user.getEmail());

        passwordResetTokenService.deletePasswordResetToken(resetToken);

        log.info("Password Reset Successfully for user {}", user.getEmail());

        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Reset Successfully")
                .build();
    }

    @Transactional
    public ApiResponse changePassword(String oldPassword, String newPassword, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getId())
                            .orElseThrow(() -> new UserNotFoundException("User Not Found!"));

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
}
