package com.aathi.authenticationsystem.dto.request;

public record ChangePasswordRequest(

        String oldPassword,
        String newPassword
) {
}
