package com.portfolioservice.service;

import com.portfolioservice.dto.CreatePortfolioRequest;
import com.portfolioservice.dto.PortfolioResponse;
import com.portfolioservice.exception.ResourceNotFoundException;
import com.portfolioservice.model.Portfolio;
import com.portfolioservice.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    //Create a new portfolio for the given investor
    public PortfolioResponse createPortfolio(Long investorId, CreatePortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .investorId(investorId)
                .name(request.getName())
                .balance(request.getBalance())
                .build();
        portfolio = portfolioRepository.save(portfolio);
        return mapToResponse(portfolio);
    }

    /**
     * Find a single portfolio by ID.
     */
    public PortfolioResponse findById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return mapToResponse(portfolio);
    }

    // Find a single portfolio by ID and enforce investor ownership.
    public PortfolioResponse findByIdForInvestor(Long id, Long investorId) {
        Portfolio portfolio = getPortfolioEntityForInvestor(id, investorId);
        return mapToResponse(portfolio);
    }

    /**
     * Find all portfolios belonging to a specific investor.
     */
    public List<PortfolioResponse> findByInvestorId(Long investorId) {
        return portfolioRepository.findByInvestorId(investorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * List every portfolio in the system (admin use).
     */
    public List<PortfolioResponse> findAll() {
        return portfolioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get the raw entity (used internally by other services).
     */
    public Portfolio getPortfolioEntity(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
    }

    public Portfolio getPortfolioEntityForInvestor(Long id, Long investorId) {
        Portfolio portfolio = getPortfolioEntity(id);
        if (!portfolio.getInvestorId().equals(investorId)) {
            throw new ResourceNotFoundException(
                    "Portfolio not found with id: " + id + " for investor: " + investorId);
        }
        return portfolio;
    }

    private PortfolioResponse mapToResponse(Portfolio p) {
        return PortfolioResponse.builder()
                .id(p.getId())
                .investorId(p.getInvestorId())
                .name(p.getName())
                .balance(p.getBalance())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
