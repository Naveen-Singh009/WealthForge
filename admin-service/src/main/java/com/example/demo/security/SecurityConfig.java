package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // === Endpoints called by investor-service via Feign ===
                        // READ: companies & stocks — INVESTOR, ADVISOR, ADMIN allowed
                        .requestMatchers(HttpMethod.GET,
                                "/api/admin/companies",
                                "/api/admin/stocks",
                                "/api/admin/stocks/**")
                        .hasAnyAuthority("ROLE_INVESTOR", "ROLE_ADVISOR", "ROLE_ADMIN")

                        // WRITE: quantity updates triggered by buy/sell — INVESTOR allowed
                        .requestMatchers(HttpMethod.PUT,
                                "/api/admin/stocks/update-quantity-buy",
                                "/api/admin/stocks/update-quantity-sell")
                        .hasAnyAuthority("ROLE_INVESTOR", "ROLE_ADVISOR", "ROLE_ADMIN")

                        // === Everything else — ADMIN only ===
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
