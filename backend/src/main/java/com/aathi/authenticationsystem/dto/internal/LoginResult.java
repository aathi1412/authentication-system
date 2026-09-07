package com.aathi.authenticationsystem.dto.internal;

import com.aathi.authenticationsystem.dto.response.LoginResponse;

import java.time.Instant;

public record LoginResult(

        LoginResponse loginResponse,
        String refreshToken,
        Instant refreshTokenExpiry
) {}
