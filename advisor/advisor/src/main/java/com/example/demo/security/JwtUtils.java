package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    
    @PostConstruct
    public void init() {
        log.info("=== Advisor Service JwtUtils initialized ===");
        log.info("JWT Secret loaded. Length: {}, First4Chars: {}",
                jwtSecret != null ? jwtSecret.length() : "NULL",
                jwtSecret != null && jwtSecret.length() >= 4 ? jwtSecret.substring(0, 4) : "N/A");
    }

    // ── Extract email (subject claim) ──────────────────────────────────────
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // ── Extract role claim (e.g. "ROLE_ADVISOR") ───────────────────────────
    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    // ── Validate token (signature + expiry) ────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token); 
            boolean notExpired = !claims.getExpiration().before(new Date());
            log.debug("Token valid. Email: {}, Exp: {}, NotExpired: {}",
                    claims.getSubject(), claims.getExpiration(), notExpired);
            return notExpired;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            log.error("Ensure advisor-service app.jwt.secret matches auth-service app.jwt.secret EXACTLY");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT error: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        log.debug("getSignKey() - secret byte length: {}", keyBytes.length);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
