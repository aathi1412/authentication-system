package com.aathi.authenticationsystem.dto.request;

import jakarta.validation.constraints.Email;

public record EmailVerificationRequest(
        @Email
        String email
) {
}
