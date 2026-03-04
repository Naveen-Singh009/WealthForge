package com.example.demo;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.config.FeignClientConfig;
import com.example.demo.dto.InvestorDTO;

@FeignClient(name = "investor-service", url = "http://localhost:8085", configuration = FeignClientConfig.class)
public interface InvestorClient {

    @GetMapping("/api/investor/investors")
    List<InvestorDTO> getAllInvestors();
}
