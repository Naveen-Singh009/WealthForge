package com.authservice.service;

import com.authservice.dto.AuthResponse;
import com.authservice.dto.LoginRequest;
import com.authservice.dto.MessageResponse;
import com.authservice.dto.RegisterRequest;
import com.authservice.model.RoleType;
import com.authservice.model.User;
import com.authservice.repository.UserRepository;
import com.authservice.security.JwtUtils;
import com.authservice.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final UserDetailsServiceImpl userDetailsService;

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + request.getEmail());
        }
        if (request.getRole() != RoleType.INVESTOR) {
            throw new IllegalArgumentException("Public registration only supports INVESTOR role");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.INVESTOR)
                .enabled(true)
                .mfaEnabled(false)
                .build();

        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    public Object login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // IF MFA ENABLED -> SEND OTP
        if (user.getMfaEnabled()) {
            String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);
            emailService.sendOtp(user.getEmail(), otp);
            return new MessageResponse("OTP sent to email. Verify to complete login.");
        }

        // NORMAL LOGIN
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String jwt = jwtUtils.generateToken(userDetails, user.getId());
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new AuthResponse(jwt, request.getEmail(), role);
    }

    // VERIFY LOGIN OTP (STEP 2)
    public AuthResponse verifyLoginOtp(String email, String otp) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null)
            throw new RuntimeException("OTP not generated");

        if (!user.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        if (user.getOtpExpiry().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String jwt = jwtUtils.generateToken(userDetails, user.getId());
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new AuthResponse(jwt, email, role);
    }

    // ENABLE / DISABLE MFA
    public MessageResponse toggleMfa(String email, boolean enable) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setMfaEnabled(enable);
        userRepository.save(user);
        return new MessageResponse("MFA updated");
    }

    // LOGOUT
    public MessageResponse logout() {
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logged out successfully. Remove token from client.");
    }
}
