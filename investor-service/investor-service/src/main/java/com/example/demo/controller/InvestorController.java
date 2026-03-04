package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Investor;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.service.InvestorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/list")
    public List<StockDTO> listMarketStocks() {
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
    public String transfer(@Valid @RequestBody PortfolioTransferRequest request, Authentication authentication) {
        return service.transferBetweenPortfolios(getAuthenticatedInvestorId(authentication), request);
    }

    @GetMapping("/history")
    public Map<String, Object> history(Authentication authentication) {
        return service.getHistory(getAuthenticatedInvestorId(authentication));
    }

    @GetMapping("/history/{portfolioId}")
    public Map<String, Object> portfolioHistory(@PathVariable Long portfolioId, Authentication authentication) {
        return service.getPortfolioHistory(getAuthenticatedInvestorId(authentication), portfolioId);
    }

    @PostMapping("/internal/register")
    public Investor registerInvestorProfile(
            @RequestHeader("X-Internal-Key") String internalKey,
            @Valid @RequestBody InvestorRegistrationRequest request) {
        return service.registerInvestorProfile(internalKey, request);
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

    @GetMapping("/investors")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Investor> listAllInvestors() {
        return service.listAllInvestors();
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
