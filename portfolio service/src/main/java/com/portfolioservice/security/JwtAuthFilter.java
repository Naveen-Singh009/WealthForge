package com.portfolioservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;


@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (token != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Email stored as subject
                String email = claims.getSubject();
                if (email == null) {
                    log.warn("JWT subject (email) is null");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Role stored as single String e.g. "ROLE_INVESTOR"
                String role = claims.get("role", String.class);
                if (role == null) {
                    log.warn("JWT 'role' claim is missing");
                    filterChain.doFilter(request, response);
                    return;
                }

                Object userIdObj = claims.get("userId");
                Long userId = null;
                if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                }

                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(role));

                // Principal = userId (Long) so PortfolioController can use it directly
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId != null ? userId : email,  // principal
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("JWT authenticated email={} role={} userId={}", email, role, userId);
            }

        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
