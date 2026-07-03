package com.aathi.authenticationsystem.security.cookies;

import com.aathi.authenticationsystem.configuration.CookiesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static com.aathi.authenticationsystem.constants.CookieConstants.REFRESH_TOKEN_COOKIE;

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

    public ResponseCookie clearRefreshTokenCookie(){

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(cookiesProperties.httpOnly())
                .secure(cookiesProperties.secure())
                .path("/")
                .sameSite(cookiesProperties.sameSite())
                .maxAge(0)
                .build();
    }
}
