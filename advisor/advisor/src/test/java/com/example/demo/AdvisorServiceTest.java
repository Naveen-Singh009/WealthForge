package com.example.demo;

import com.example.demo.entity.Advisor;
import com.example.demo.repository.AdvisorRepository;
import com.example.demo.repository.InvestorAllocationRepository;
import com.example.demo.repository.AdviceRepository;
import com.example.demo.service.AdvisorService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdvisorServiceTest {

    @Mock
    private AdvisorRepository advisorRepo;

    @Mock
    private InvestorAllocationRepository allocationRepo;

    @Mock
    private AdviceRepository adviceRepo;

    @InjectMocks
    private AdvisorService service;

    @Test
    void testRegisterAdvisor() {

        Advisor advisor = new Advisor();
        advisor.setName("Rahul");
        advisor.setEmail("rahul@gmail.com");

        when(advisorRepo.save(any(Advisor.class)))
                .thenReturn(advisor);

        Advisor saved = service.register(advisor);

        assertNotNull(saved);
        assertEquals("Rahul", saved.getName());
        assertEquals("rahul@gmail.com", saved.getEmail());

        verify(advisorRepo).save(any(Advisor.class));
    }
}