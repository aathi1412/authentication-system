package com.aathi.authenticationsystem.exception;

public class InvalidOrExpiredVerificationTokenException extends RuntimeException {
    public InvalidOrExpiredVerificationTokenException(String message) {
        super(message);
    }
}
