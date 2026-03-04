package com.portfolioservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceResponse {
    private Long portfolioId;
    private String portfolioName;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal gainPercent;
    private List<HoldingResponse> holdings;
}
