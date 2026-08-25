package com.aathi.authenticationsystem.dto.request;

public record ChangePasswordRequest(

        String currentPassword,
        String newPassword
) {
}
