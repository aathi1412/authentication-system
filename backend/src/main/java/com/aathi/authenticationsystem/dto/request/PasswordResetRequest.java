package com.aathi.authenticationsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(

        String token,

        @NotBlank
        @Size(min = 8)
        String password
) {}
