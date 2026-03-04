package com.example.demo.controller;

import com.example.demo.dto.AdvisorDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.security.AuthEntryPointJwt;
import com.example.demo.security.JwtAuthFilter;
import com.example.demo.service.InvestorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvestorController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvestorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestorService investorService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    void testCompanyList() throws Exception {
        Mockito.when(investorService.getCompanyList()).thenReturn(Collections.<CompanyDTO>emptyList());

        mockMvc.perform(get("/api/investor/companyList"))
                .andExpect(status().isOk());
    }

    @Test
    void testAdvisorList() throws Exception {
        Mockito.when(investorService.getAdvisorList()).thenReturn(Collections.<AdvisorDTO>emptyList());

        mockMvc.perform(get("/api/investor/searchAdvisor"))
                .andExpect(status().isOk());
    }
}
