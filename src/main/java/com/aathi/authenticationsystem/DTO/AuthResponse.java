package com.aathi.authenticationsystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {

    private String message;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserResponse userResponse;
}
