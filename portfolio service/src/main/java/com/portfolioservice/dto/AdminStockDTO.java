package com.portfolioservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminStockDTO {
    private String symbol;
    private String name;
    private String sector;
    private BigDecimal currentPrice;
    private Integer availableQuantity;
}
