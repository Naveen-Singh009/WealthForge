package com.portfolioservice.service;

import com.portfolioservice.dto.HoldingResponse;
import com.portfolioservice.dto.OverallPerformanceResponse;
import com.portfolioservice.dto.PerformanceResponse;
import com.portfolioservice.model.Portfolio;
import com.portfolioservice.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PortfolioService portfolioService;
    private final HoldingService holdingService;
    private final PortfolioRepository portfolioRepository;

    //Calculate portfolio performance: total invested, current value, P&L, gain %
    public PerformanceResponse getPerformance(Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioEntity(portfolioId);
        List<HoldingResponse> holdings = holdingService.getHoldingsForPortfolio(portfolioId);

        BigDecimal totalInvested = BigDecimal.ZERO; 
        BigDecimal currentValue = BigDecimal.ZERO;

        for (HoldingResponse h : holdings) {
            BigDecimal invested = h.getAveragePrice().multiply(h.getQuantity());
            totalInvested = totalInvested.add(invested);
            currentValue = currentValue.add(h.getCurrentValue());
        }

        BigDecimal profitLoss = currentValue.subtract(totalInvested);
        BigDecimal gainPercent = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            gainPercent = profitLoss
                    .divide(totalInvested, 6, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return PerformanceResponse.builder()
                .portfolioId(portfolio.getId())
                .portfolioName(portfolio.getName())
                .totalInvested(totalInvested.setScale(6, RoundingMode.HALF_UP))
                .currentValue(currentValue.setScale(6, RoundingMode.HALF_UP))
                .profitLoss(profitLoss.setScale(6, RoundingMode.HALF_UP))
                .gainPercent(gainPercent.setScale(2, RoundingMode.HALF_UP))
                .holdings(holdings)
                .build();
    }

    public OverallPerformanceResponse getOverallPerformance(Long investorId) {
        List<Portfolio> portfolios = portfolioRepository.findByInvestorId(investorId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentMarketValue = BigDecimal.ZERO;
        BigDecimal cashBalance = BigDecimal.ZERO;

        for (Portfolio portfolio : portfolios) {
            cashBalance = cashBalance.add(portfolio.getBalance());
            List<HoldingResponse> holdings = holdingService.getHoldingsForPortfolio(portfolio.getId());
            for (HoldingResponse h : holdings) {
                BigDecimal invested = h.getAveragePrice().multiply(h.getQuantity());
                totalInvested = totalInvested.add(invested);
                currentMarketValue = currentMarketValue.add(h.getCurrentValue());
            }
        }

        BigDecimal profitLoss = currentMarketValue.subtract(totalInvested);
        BigDecimal gainPercent = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            gainPercent = profitLoss
                    .divide(totalInvested, 6, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        BigDecimal netWorth = currentMarketValue.add(cashBalance);

        return OverallPerformanceResponse.builder()
                .totalPortfolios(portfolios.size())
                .totalInvested(totalInvested.setScale(6, RoundingMode.HALF_UP))
                .currentMarketValue(currentMarketValue.setScale(6, RoundingMode.HALF_UP))
                .cashBalance(cashBalance.setScale(6, RoundingMode.HALF_UP))
                .netWorth(netWorth.setScale(6, RoundingMode.HALF_UP))
                .profitLoss(profitLoss.setScale(6, RoundingMode.HALF_UP))
                .gainPercent(gainPercent.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
