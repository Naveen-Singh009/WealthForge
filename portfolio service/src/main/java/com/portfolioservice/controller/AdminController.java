package com.portfolioservice.controller;

import com.portfolioservice.dto.ApiResponse;
import com.portfolioservice.dto.PortfolioResponse;
import com.portfolioservice.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/portfolios")
@RequiredArgsConstructor
public class AdminController {

    private final PortfolioService portfolioService;

    //Admin lists every portfolio 

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getAllPortfolios() {
        List<PortfolioResponse> portfolios = portfolioService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("All portfolios retrieved", portfolios));
    }
}
