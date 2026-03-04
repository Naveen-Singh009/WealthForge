package com.authservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ADMIN access
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDashboard_shouldAllowAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INVESTOR")
    void adminDashboard_shouldDenyNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    // ADVISOR
    @Test
    @WithMockUser(roles = "ADVISOR")
    void advisorClients_shouldAllowAdvisor() throws Exception {
        mockMvc.perform(get("/api/advisor/clients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INVESTOR")
    void advisorClients_shouldDenyInvestor() throws Exception {
        mockMvc.perform(get("/api/advisor/clients"))
                .andExpect(status().isForbidden());
    }

    // INVESTOR
    @Test
    @WithMockUser(roles = "INVESTOR")
    void investorPortfolio_shouldAllowInvestor() throws Exception {
        mockMvc.perform(get("/api/investor/portfolio"))
                .andExpect(status().isOk());
    }

    @Test
    void investorPortfolio_shouldReturnUnauthorized_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/investor/portfolio"))
                .andExpect(status().isUnauthorized());
    }
}