package com.example.demo;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Investor;

@FeignClient(name = "investor-service")
public interface InvestorClient {

    @GetMapping("/api/investor/list")
    List<Investor> getAllInvestors();
}