package com.aathi.authenticationsystem.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.cookie")
public record CookiesProperties(
        boolean httpOnly,
        boolean secure,
        String path,
        String sameSite,
        Duration maxAge
) {}
