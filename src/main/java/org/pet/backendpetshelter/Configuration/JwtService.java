package org.pet.backendpetshelter.Configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key accessKey;
    private final Key refreshKey;
    private final long accessExpSeconds;
    private final long refreshExpSeconds;

    public JwtService(JwtProperties props) {
        this.accessKey = Keys.hmacShaKeyFor(props.getAccessSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(props.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
        this.accessExpSeconds = props.getAccessExpirationSeconds();
        this.refreshExpSeconds = props.getRefreshExpirationSeconds();
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, accessKey, accessExpSeconds);
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, refreshKey, refreshExpSeconds);
    }

    private String buildToken(String subject, Map<String, Object> claims, Key key, long expSecs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // Must be AFTER setClaims to avoid being overwritten
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expSecs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseAccessToken(String token) {
        return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
    }

    public Jws<Claims> parseRefreshToken(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
    }
}