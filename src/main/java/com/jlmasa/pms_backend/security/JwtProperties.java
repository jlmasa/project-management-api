package com.jlmasa.pms_backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, int accessTtlMinutes, int refreshTtlDays) {

    public JwtProperties{
        if(secret == null || secret.getBytes(StandardCharsets.UTF_8).length <32){
            throw new IllegalStateException("app.jwt.secret must be at least 256 bits (32 bytes) long.");
        }
    }
}
