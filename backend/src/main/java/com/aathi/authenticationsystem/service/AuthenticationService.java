package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.configuration.JwtProperties;
import com.aathi.authenticationsystem.dto.internal.LoginResult;
import com.aathi.authenticationsystem.dto.internal.RefreshResult;
import com.aathi.authenticationsystem.dto.request.LoginRequest;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.AccessTokenResponse;
import com.aathi.authenticationsystem.dto.response.ApiResponse;
import com.aathi.authenticationsystem.dto.response.LoginResponse;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.dto.user.UserResponse;
import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.enums.ActivityType;
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
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private final ActivityLogsService activityLogsService;

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

        activityLogsService.saveActivityLog(
                savedUser,
                "User Register",
                ActivityType.ACCOUNT_CREATED,
                "Register successful",
                ActivityCategory.AUTHENTICATION
        );

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
        User user = userService.getUserByEmail(request.getEmail());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException ex){
            log.error("Account Not Verified for user {}", request.getEmail());
            activityLogsService.saveActivityLog(
                    user,
                    "Email Not Verified",
                    ActivityType.EMAIL_VERIFICATION_FAILED,
                    "Account Not Verified",
                    ActivityCategory.AUTHENTICATION
            );
            throw new AccountNotVerifiedException("Account Not Verified, Please Verify with Email.");
        } catch (LockedException ex) {

            log.error("Account is locked for user {}", request.getEmail());
            activityLogsService.saveActivityLog(
                    user,
                    "Account Locked",
                    ActivityType.ACCOUNT_LOCKED,
                    "Account Locked, too many failed login attempts",
                    ActivityCategory.AUTHENTICATION
            );
            throw new AccountLockedException("Your account is locked. Please try again later or contact support.");

        }catch (BadCredentialsException ex){
            log.error("invalid Email or Password, error: {}", ex.getMessage());

            userService.increaseFailedLoginAttempt(request.getEmail());

            log.info("login attempt failed for user : {}", ex.getMessage());

            activityLogsService.saveActivityLog(
                    user,
                    "Log in Failed",
                    ActivityType.LOGIN_FAILED,
                    "Incorrect Email or Password entered",
                    ActivityCategory.AUTHENTICATION
            );

            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) Objects.requireNonNull(authentication.getPrincipal());

        String accessToken = jwtService.generateAccessToken(customUserDetails.user());
        log.info("access token generated");

        Instant now = Instant.now();
        Instant sessionExpiryDate = request.isRememberMe()
                ? now.plus(30, ChronoUnit.DAYS)
                : now.plus(1, ChronoUnit.DAYS);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customUserDetails.user(), request.isRememberMe(), sessionExpiryDate);
        log.info("refresh token generated");

        log.info("Login Successful for user {}", request.getEmail());

        activityLogsService.saveActivityLog(
                customUserDetails.user(),
                "Logged in",
                ActivityType.LOGIN_SUCCESS,
                "login successful",
                ActivityCategory.AUTHENTICATION
        );

        userService.resetFailedLoginAttempt(customUserDetails.getUsername());
        customUserDetails.user().setLastLogin(Instant.now());
        User savedUser = userRepository.save(customUserDetails.user());

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
                                .id(savedUser.getId())
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole())
                                .phone(savedUser.getPhone())
                                .bio(savedUser.getBio())
                                .build()
                        )
                        .build(),
                refreshToken.getToken(),
                refreshToken.getExpiryDate()
        );
    }

    @Transactional
    public RefreshResult refresh(String token){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(token);

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(user, refreshToken);

        log.info("new access token generated for user {}", user.getEmail());
        log.info("new refresh token generated for user {}", user.getEmail());

        return new RefreshResult(
                AccessTokenResponse.builder()
                        .message("new access token generated")
                        .tokenType(TOKEN_TYPE)
                        .accessToken(accessToken)
                        .expiresIn(jwtProperties.accessTokenExpiration().toSeconds())
                        .build(),
                newRefreshToken.getToken(),
                newRefreshToken.getExpiryDate()
        );
    }

    public void logout(User user, String refreshToken){

        refreshTokenService.revokeRefreshToken(user.getId(),refreshToken);
        log.info("userId {} logout successfully", user.getId());
        activityLogsService.saveActivityLog(
                user,
                "User Logout",
                ActivityType.LOGOUT,
                "User Logout successfully",
                ActivityCategory.AUTHENTICATION
        );
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

            activityLogsService.saveActivityLog(
                    user,
                    "Email Verified",
                    ActivityType.EMAIL_VERIFIED,
                    "Account Verified Successfully",
                    ActivityCategory.AUTHENTICATION
            );

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
        activityLogsService.saveActivityLog(
                user,
                "verification email Requested",
                ActivityType.EMAIL_VERIFIED,
                "verification email Requested",
                ActivityCategory.AUTHENTICATION
        );
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

                activityLogsService.saveActivityLog(
                        user,
                        "Forgot password Email Requested",
                        ActivityType.PASSWORD_RESET_REQUESTED,
                        "Forgot password Email Requested",
                        ActivityCategory.AUTHENTICATION
                );
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

        activityLogsService.saveActivityLog(
                user,
                "Password Reset",
                ActivityType.PASSWORD_CHANGED,
                "Password Reset Successfully",
                ActivityCategory.AUTHENTICATION
        );

        return ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.getReasonPhrase())
                .message("Password Reset Successfully")
                .build();
    }
}
