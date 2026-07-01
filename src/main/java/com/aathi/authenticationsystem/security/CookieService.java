package com.aathi.authenticationsystem.security;

import com.aathi.authenticationsystem.configuration.CookiesProperties;
import com.aathi.authenticationsystem.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static com.aathi.authenticationsystem.constants.SecurityConstants.REFRESH_TOKEN_COOKIE;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookiesProperties cookiesProperties;

    public ResponseCookie createRefreshTokenCookie(String refreshToken){

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(cookiesProperties.httpOnly())
                .secure(cookiesProperties.secure())
                .path(cookiesProperties.path())
                .sameSite(cookiesProperties.sameSite())
                .maxAge(cookiesProperties.maxAge())
                .build();
    }
}
