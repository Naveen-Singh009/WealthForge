package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InvestorServiceTest {

    @Autowired
    InvestorService service;


    // ✅ Test Service Loaded
    @Test
    public void testServiceNotNull() {

        assertNotNull(service);

    }

}