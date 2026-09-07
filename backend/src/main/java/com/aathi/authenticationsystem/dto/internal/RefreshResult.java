package com.aathi.authenticationsystem.dto.internal;

import com.aathi.authenticationsystem.dto.response.AccessTokenResponse;

import java.time.Instant;

public record RefreshResult(

        AccessTokenResponse accessToken,
        String refreshToken,
        Instant refreshTokenExpiry
) {}
