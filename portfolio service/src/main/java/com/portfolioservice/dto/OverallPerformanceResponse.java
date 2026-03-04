package com.portfolioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverallPerformanceResponse {
    private int totalPortfolios;
    private BigDecimal totalInvested;
    private BigDecimal currentMarketValue;
    private BigDecimal cashBalance;
    private BigDecimal netWorth;
    private BigDecimal profitLoss;
    private BigDecimal gainPercent;
}
