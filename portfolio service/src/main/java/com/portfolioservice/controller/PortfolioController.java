package com.portfolioservice.controller;

import com.portfolioservice.dto.*;
import com.portfolioservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final HoldingService holdingService;
    private final PerformanceService performanceService;
    private final TransferService transferService;
    private final PortfolioTradingService portfolioTradingService;
    private final TransactionService transactionService;

    //Create a new portfolio for the authenticated investor
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            Authentication authentication) {

       Long investorId = getInvestorId(authentication);
//        Long investorId = 1L;   // temporary testing
        
        PortfolioResponse response = portfolioService.createPortfolio(investorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Portfolio created successfully", response));
    }

    //Get all portfolios belonging to the authenticated investor.

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getMyPortfolios(
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
    	
        //Long investorId = 1L; // temp testing
        List<PortfolioResponse> portfolios = portfolioService.findByInvestorId(investorId);
        return ResponseEntity.ok(ApiResponse.ok("Portfolios retrieved", portfolios));
    }

    //Get a specific portfolio by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolioById(
            @PathVariable Long id,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        PortfolioResponse response = portfolioService.findByIdForInvestor(id, investorId);
        return ResponseEntity.ok(ApiResponse.ok("Portfolio retrieved", response));
    }

    //Get all holdings for a portfolio
    @GetMapping("/{id}/holdings")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getHoldings(
            @PathVariable Long id,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        portfolioService.getPortfolioEntityForInvestor(id, investorId);
        List<HoldingResponse> holdings = holdingService.getHoldingsForPortfolio(id);
        return ResponseEntity.ok(ApiResponse.ok("Holdings retrieved", holdings));
    }

    //Get performance analytics (P&L) for a portfolio
    @GetMapping("/{id}/performance")
    public ResponseEntity<ApiResponse<PerformanceResponse>> getPerformance(
            @PathVariable Long id,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        portfolioService.getPortfolioEntityForInvestor(id, investorId);
        PerformanceResponse perf = performanceService.getPerformance(id);
        return ResponseEntity.ok(ApiResponse.ok("Performance analytics retrieved", perf));
    }

    //Get asset allocation breakdown for a portfolio
    @GetMapping("/{id}/allocation")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getAllocation(
            @PathVariable Long id,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        portfolioService.getPortfolioEntityForInvestor(id, investorId);
        List<HoldingResponse> holdings = holdingService.getHoldingsForPortfolio(id);
        return ResponseEntity.ok(ApiResponse.ok("Asset allocation retrieved", holdings));
    }

    //Transfer funds between two portfolios owned by the authenticated invest
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Void>> transferFunds(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

       Long investorId = getInvestorId(authentication);
        //Long investorId = 1L;
        transferService.transferFunds(investorId, request);
        return ResponseEntity.ok(ApiResponse.ok("Transfer completed successfully", null));
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<ApiResponse<Void>> buyAsset(
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest request,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        portfolioTradingService.buyAsset(investorId, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Buy order executed successfully", null));
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<ApiResponse<Void>> sellAsset(
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest request,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        portfolioTradingService.sellAsset(investorId, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Sell order executed successfully", null));
    }

    @GetMapping("/overall-performance")
    public ResponseEntity<ApiResponse<OverallPerformanceResponse>> getOverallPerformance(
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        OverallPerformanceResponse response = performanceService.getOverallPerformance(investorId);
        return ResponseEntity.ok(ApiResponse.ok("Overall performance retrieved", response));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getPortfolioTransactions(
            @PathVariable Long id,
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        List<TransactionResponse> transactions = transactionService.getPortfolioTransactions(investorId, id);
        return ResponseEntity.ok(ApiResponse.ok("Portfolio transactions retrieved", transactions));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions(
            Authentication authentication) {

        Long investorId = getInvestorId(authentication);
        List<TransactionResponse> transactions = transactionService.getAllTransactionsForInvestor(investorId);
        return ResponseEntity.ok(ApiResponse.ok("Transaction history retrieved", transactions));
    }


    //Extract investor ID from JWT authentication principal
    private Long getInvestorId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof String principalText) {
            try {
                return Long.parseLong(principalText);
            } catch (NumberFormatException ignored) {
                throw new IllegalStateException("JWT does not contain a numeric userId claim");
            }
        }
        throw new IllegalStateException("Invalid authentication principal");
    }
}
