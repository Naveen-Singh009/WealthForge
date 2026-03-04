package com.example.demo.security;

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

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Health check — open
                        .requestMatchers("/actuator/**").permitAll()

                        // Advisor self-registration — public
                        .requestMatchers("/api/advisor/register").permitAll()

                        // === Endpoints called by investor-service via Feign ===
                        // Investors can read advisor list and use the chatbot
                        .requestMatchers(
                                "/api/advisor/list/all",
                                "/api/advisor/chatbot/ask")
                        .hasAnyAuthority("ROLE_INVESTOR", "ROLE_ADVISOR", "ROLE_ADMIN")

                        // === All other advisor endpoints — ADVISOR or ADMIN only ===
                        .requestMatchers("/api/advisor/**")
                        .hasAnyAuthority("ROLE_ADVISOR", "ROLE_ADMIN")

                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}