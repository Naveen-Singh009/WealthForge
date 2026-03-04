package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class InvestorRegistrationRequest {

    @NotNull(message = "Investor ID is required")
    private Long investorId;

    @NotBlank(message = "Investor name is required")
    private String investorName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
