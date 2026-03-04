package com.example.demo.client;

import com.example.demo.config.FeignClientConfig;
import com.example.demo.dto.PortfolioCreateRequest;
import com.example.demo.dto.PortfolioTradeRequest;
import com.example.demo.dto.PortfolioTransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feign client for portfolio-service (port 8088).
 * Allows investor-service to query and manage portfolios.
 */
@FeignClient(name = "portfolio-service", url = "http://localhost:8088", configuration = FeignClientConfig.class)
public interface PortfolioClient {

    @PostMapping("/api/portfolios")
    ResponseEntity<Map<String, Object>> createPortfolio(@RequestBody PortfolioCreateRequest request);

    // Get all portfolios for the authenticated investor
    @GetMapping("/api/portfolios/my")
    ResponseEntity<Map<String, Object>> getMyPortfolios();

    // Get a specific portfolio by ID
    @GetMapping("/api/portfolios/{id}")
    ResponseEntity<Map<String, Object>> getPortfolioById(@PathVariable("id") Long id);

    // Get holdings for a portfolio
    @GetMapping("/api/portfolios/{id}/holdings")
    ResponseEntity<Map<String, Object>> getHoldings(@PathVariable("id") Long id);

    // Get performance analytics for a portfolio
    @GetMapping("/api/portfolios/{id}/performance")
    ResponseEntity<Map<String, Object>> getPerformance(@PathVariable("id") Long id);

    @PostMapping("/api/portfolios/{id}/buy")
    ResponseEntity<Map<String, Object>> buyInPortfolio(@PathVariable("id") Long id,
                                                       @RequestBody PortfolioTradeRequest request);

    @PostMapping("/api/portfolios/{id}/sell")
    ResponseEntity<Map<String, Object>> sellInPortfolio(@PathVariable("id") Long id,
                                                        @RequestBody PortfolioTradeRequest request);

    @PostMapping("/api/portfolios/transfer")
    ResponseEntity<Map<String, Object>> transferBetweenPortfolios(@RequestBody PortfolioTransferRequest request);

    @GetMapping("/api/portfolios/overall-performance")
    ResponseEntity<Map<String, Object>> getOverallPerformance();
}
