package com.portfolioservice.dto;

import com.portfolioservice.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Long portfolioId;
    private TransactionType type;
    private String assetSymbol;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
