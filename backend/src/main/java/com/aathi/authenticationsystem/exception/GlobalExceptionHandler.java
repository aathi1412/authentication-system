package com.aathi.authenticationsystem.exception;

import com.aathi.authenticationsystem.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .build();

         return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentialsException(InvalidCredentialsException ex){

        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponse> handleAccountNotVerifiedException(AccountNotVerifiedException ex){
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex){
        ApiResponse error = ApiResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleResourceAccessDeniedException(ResourceAccessDeniedException ex){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleVerificationTokenExpiredException(VerificationTokenExpiredException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidVerificationTokenException(InvalidVerificationTokenException ex){

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
    public ResponseEntity<ApiResponse> handleInvalidPasswordResetTokenException(InvalidPasswordResetTokenException ex){

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
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex){

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
    public ResponseEntity<ApiResponse> handleBadRequestException (BadRequestException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timeStamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

}
