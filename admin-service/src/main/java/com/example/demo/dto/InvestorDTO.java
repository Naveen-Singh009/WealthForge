package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestorDTO {
    private Long investorId;
    private String investorName;
    private String email;
    private BigDecimal balance;
}
