package com.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

        // ─── ADMIN only ───────────────────────────────────────────────────────────
        @GetMapping("/admin/dashboard")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, String>> adminDashboard() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "Admin Dashboard",
                                "access", "ADMIN only",
                                "message", "Welcome, Administrator! Full system access granted."));
        }

        @GetMapping("/admin/users")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, String>> adminUsers() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "User Management",
                                "access", "ADMIN only",
                                "message", "List of all registered users."));
        }

        // ─── ADVISOR + ADMIN ──────────────────────────────────────────────────────
        @GetMapping("/advisor/clients")
        
        @PreAuthorize("hasAnyRole('ADVISOR', 'ADMIN')")
        public ResponseEntity<Map<String, String>> advisorClients() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "Client Management",
                                "access", "ADVISOR and ADMIN",
                                "message", "List of advisor clients."));
        }

        @GetMapping("/advisor/recommendations")
        @PreAuthorize("hasAnyRole('ADVISOR', 'ADMIN')")
        public ResponseEntity<Map<String, String>> advisorRecommendations() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "Investment Recommendations",
                                "access", "ADVISOR and ADMIN",
                                "message", "Manage investment recommendations for clients."));
        }

        // ─── INVESTOR + ADVISOR + ADMIN ───────────────────────────────────────────
        @GetMapping("/investor/portfolio")
        @PreAuthorize("hasAnyRole('INVESTOR', 'ADVISOR', 'ADMIN')")
        public ResponseEntity<Map<String, String>> investorPortfolio() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "Portfolio",
                                "access", "INVESTOR, ADVISOR, and ADMIN",
                                "message", "View your investment portfolio."));
        }

        @GetMapping("/investor/trading")
        @PreAuthorize("hasAnyRole('INVESTOR', 'ADVISOR', 'ADMIN')")
        public ResponseEntity<Map<String, String>> investorTrading() {
                return ResponseEntity.ok(Map.of(
                                "endpoint", "Trading",
                                "access", "INVESTOR, ADVISOR, and ADMIN",
                                "message", "Access trading features."));
        }
}
