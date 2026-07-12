package com.aathi.authenticationsystem.exception;

public class InvalidEmailDomainException extends RuntimeException {
    public InvalidEmailDomainException(String message) {
        super(message);
    }
}
