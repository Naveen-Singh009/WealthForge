package com.example.demo.dto;

import java.math.BigDecimal;

public class StockDTO {

    private Long id;
    private String symbol;
    private BigDecimal currentPrice;
    private Long availableQuantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public Long getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Long availableQuantity) { this.availableQuantity = availableQuantity; }
}
