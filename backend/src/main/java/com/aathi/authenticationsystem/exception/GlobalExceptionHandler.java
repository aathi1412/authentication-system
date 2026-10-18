package com.aathi.authenticationsystem.exception;

import com.aathi.authenticationsystem.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiResponse response = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for one or more fields")
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLockedException(AccountLockedException ex) {
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED).body(error);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponse> handleAccountNotVerifiedException(AccountNotVerifiedException ex) {
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleResourceAccessDeniedException(ResourceAccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message("Access Denied: You do not have permission to perform this action.")
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidOrExpiredVerificationTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidVerificationTokenException(InvalidOrExpiredVerificationTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidPasswordResetTokenException(InvalidPasswordResetTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidEmailDomainException.class)
    public ResponseEntity<ApiResponse> handleInvalidEmailDomainException(InvalidEmailDomainException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Uploaded file exceeds the maximum allowed size of 5MB.")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception caught by GlobalExceptionHandler: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message("An unexpected error occurred. Please try again later.")
                        .build());
    }
}
