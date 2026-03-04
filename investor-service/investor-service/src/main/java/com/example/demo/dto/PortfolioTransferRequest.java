package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PortfolioTransferRequest {
    @NotNull(message = "Source portfolio ID required")
    private Long fromPortfolioId;

    @NotNull(message = "Destination portfolio ID required")
    private Long toPortfolioId;

    @NotNull(message = "Amount required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    public Long getFromPortfolioId() {
        return fromPortfolioId;
    }

    public void setFromPortfolioId(Long fromPortfolioId) {
        this.fromPortfolioId = fromPortfolioId;
    }

    public Long getToPortfolioId() {
        return toPortfolioId;
    }

    public void setToPortfolioId(Long toPortfolioId) {
        this.toPortfolioId = toPortfolioId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
