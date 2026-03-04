package com.portfolioservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponse {
    private Long id;
    private Long investorId;
    private String name;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
