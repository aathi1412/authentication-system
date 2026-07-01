package com.aathi.authenticationsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessTokenResponse {

    private String message;
    private String tokenType;
    private String accessToken;
    private long expiresIn;
}
