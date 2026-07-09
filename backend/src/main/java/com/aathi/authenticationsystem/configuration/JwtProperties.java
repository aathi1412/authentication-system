package com.aathi.authenticationsystem.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(

        String secretKey,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration
) {}
