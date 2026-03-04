package com.portfolioservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Health check - open
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**")
                            .hasAnyAuthority("ROLE_ADMIN")

                        // Advisor endpoints (advisor + admin)
                        .requestMatchers("/api/advisor/**")
                            .hasAnyAuthority("ROLE_ADVISOR", "ROLE_ADMIN")

                        // Investor portfolio endpoints (investor + advisor + admin)
                        .requestMatchers("/api/portfolios/**")
                            .hasAnyAuthority("ROLE_INVESTOR", "ROLE_ADVISOR", "ROLE_ADMIN")

                        // Everything else - must be authenticated
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
