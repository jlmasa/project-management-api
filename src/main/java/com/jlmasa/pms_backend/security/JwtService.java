package com.jlmasa.pms_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {

    private final SecretKey key;
    private JwtProperties properties;
    private final Clock clock;

    public JwtService(JwtProperties properties, Clock clock){
        this.properties = properties;
        this.clock = clock;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }
    public String issueAccessToken(UUID userId, UUID organizationId, UUID membershipId){
        Instant now = clock.instant();
        Instant expiry = now.plus(Duration.ofMinutes(properties.accessTtlMinutes()));
        return Jwts.builder().subject(userId.toString()).claim("org", organizationId.toString()).claim("mem", membershipId.toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
    public int accessTtlSeconds() {
        return properties.accessTtlMinutes() * 60;
    }
}
