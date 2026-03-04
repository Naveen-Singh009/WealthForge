package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Holding;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.service.InvestorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investor")
@RequiredArgsConstructor
public class InvestorController {

    private final InvestorService service;

    @GetMapping("/companyList")
    public List<CompanyDTO> getCompanies() {
        return service.getCompanyList();
    }

    @GetMapping("/stockList")
    public List<StockDTO> getStocks() {
        return service.getStockList();
    }

    @GetMapping("/searchAdvisor")
    public List<AdvisorDTO> advisor() {
        return service.getAdvisorList();
    }

    @GetMapping("/getAdvice")
    public String getAdvice(@RequestParam("question") String question) {
        return service.getAdvice(question);
    }

    @PostMapping("/buy")
    public String buyStock(@Valid @RequestBody BuyRequestDTO dto, Authentication authentication) {
        return service.buyStock(getAuthenticatedInvestorId(authentication), dto);
    }

    @PostMapping("/sell")
    public String sell(@Valid @RequestBody SellRequestDTO dto, Authentication authentication) {
        return service.sellStock(getAuthenticatedInvestorId(authentication), dto);
    }

    @PostMapping("/transfer")
    public String transfer(@Valid @RequestBody TransferRequestDTO dto, Authentication authentication) {
        return service.transferMoney(getAuthenticatedInvestorId(authentication), dto);
    }

    @GetMapping("/transactions/{investorId}")
    public List<Transaction> history(@PathVariable Long investorId, Authentication authentication) {
        Long authInvestorId = getAuthenticatedInvestorId(authentication);
        if (!authInvestorId.equals(investorId)) {
            throw new InvalidDataException("You can only view your own transactions");
        }
        return service.history(authInvestorId);
    }

    @GetMapping("/holding/{investorId}")
    public List<Holding> getHolding(@PathVariable Long investorId, Authentication authentication) {
        Long authInvestorId = getAuthenticatedInvestorId(authentication);
        if (!authInvestorId.equals(investorId)) {
            throw new InvalidDataException("You can only view your own holdings");
        }
        return service.getHolding(authInvestorId);
    }

    @GetMapping("/advisor/list/all")
    public List<AdvisorDTO> getAllAdvisor() {
        return service.getAdvisorList();
    }

    @PostMapping("/portfolios")
    public Map<String, Object> createPortfolio(@Valid @RequestBody PortfolioCreateRequest request) {
        return service.createPortfolio(request);
    }

    @GetMapping("/portfolios")
    public Map<String, Object> getMyPortfolios() {
        return service.getMyPortfolios();
    }

    @GetMapping("/portfolios/{portfolioId}")
    public Map<String, Object> getPortfolioById(@PathVariable Long portfolioId) {
        return service.getPortfolioById(portfolioId);
    }

    @GetMapping("/portfolios/{portfolioId}/holdings")
    public Map<String, Object> getPortfolioHoldings(@PathVariable Long portfolioId) {
        return service.getPortfolioHoldings(portfolioId);
    }

    @GetMapping("/portfolios/{portfolioId}/performance")
    public Map<String, Object> getPortfolioPerformance(@PathVariable Long portfolioId) {
        return service.getPortfolioPerformance(portfolioId);
    }

    @PostMapping("/portfolios/transfer")
    public Map<String, Object> transferBetweenPortfolios(@Valid @RequestBody PortfolioTransferRequest request) {
        return service.transferBetweenPortfolios(request);
    }

    @GetMapping("/portfolios/overall-performance")
    public Map<String, Object> getOverallPerformance() {
        return service.getOverallPerformance();
    }

    private Long getAuthenticatedInvestorId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof String principalText) {
            try {
                return Long.parseLong(principalText);
            } catch (NumberFormatException ignored) {
                throw new InvalidDataException("JWT userId claim is missing or invalid");
            }
        }
        throw new InvalidDataException("Invalid authentication principal");
    }
}
