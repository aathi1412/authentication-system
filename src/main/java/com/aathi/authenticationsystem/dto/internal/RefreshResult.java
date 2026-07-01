package com.aathi.authenticationsystem.dto.internal;

import com.aathi.authenticationsystem.dto.response.AccessTokenResponse;

public record RefreshResult(

        AccessTokenResponse accessToken,
        String refreshToken
) {}
