package com.authservice.controller;

import com.authservice.dto.*;
import com.authservice.security.JwtAuthFilter;
import com.authservice.security.JwtUtils;
import com.authservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ disables security filters
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // ✅ Mock security dependencies to avoid loading full security
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ LOGIN TEST (JWT response)
    @Test
    void shouldLoginUser() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("teja@gmail.com");
        request.setPassword("1234");

        AuthResponse response =
                new AuthResponse("jwt-token", "teja@gmail.com", "ROLE_INVESTOR");

        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    // ✅ VERIFY OTP TEST
    @Test
    void shouldVerifyLoginOtp() throws Exception {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("teja@gmail.com");
        request.setOtp("123456");

        AuthResponse response =
                new AuthResponse("jwt-token", "teja@gmail.com", "ROLE_INVESTOR");

        Mockito.when(authService.verifyLoginOtp("teja@gmail.com", "123456"))
                .thenReturn(response);

        mockMvc.perform(post("/auth/verify-login-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    // ✅ ENABLE MFA TEST
    @Test
    void shouldEnableMfa() throws Exception {

        Mockito.when(authService.toggleMfa("teja@gmail.com", true))
                .thenReturn(new MessageResponse("MFA enabled"));

        mockMvc.perform(post("/auth/mfa")
                        .param("email", "teja@gmail.com")
                        .param("enable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("MFA enabled"));
    }

    // ✅ LOGOUT TEST
    @Test
    void shouldLogout() throws Exception {

        Mockito.when(authService.logout())
                .thenReturn(new MessageResponse("Logged out successfully"));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Logged out successfully"));
    }
}
