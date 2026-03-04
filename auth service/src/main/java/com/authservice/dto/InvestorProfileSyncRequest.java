package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestorProfileSyncRequest {
    private Long investorId;
    private String investorName;
    private String email;
    private BigDecimal initialBalance;
}
