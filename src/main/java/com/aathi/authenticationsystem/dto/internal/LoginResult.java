package com.aathi.authenticationsystem.dto.internal;

import com.aathi.authenticationsystem.dto.response.LoginResponse;

public record LoginResult(

        LoginResponse loginResponse,
        String refreshToken
) {}
