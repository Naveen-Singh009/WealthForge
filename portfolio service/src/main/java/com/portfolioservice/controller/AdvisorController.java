package com.portfolioservice.controller;

import com.portfolioservice.dto.ApiResponse;
import com.portfolioservice.dto.PortfolioResponse;
import com.portfolioservice.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advisor/portfolios")
@RequiredArgsConstructor 
public class AdvisorController {

    private final PortfolioService portfolioService;

    //Advisor views all portfolios for a specific investor
    @GetMapping("/{investorId}")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfoliosByInvestor(
            @PathVariable Long investorId) {

        List<PortfolioResponse> portfolios = portfolioService.findByInvestorId(investorId);
        return ResponseEntity.ok(ApiResponse.ok("Investor portfolios retrieved", portfolios));
    }
}
