package com.portfolioservice.service;

import com.portfolioservice.client.AdminMarketClient;
import com.portfolioservice.dto.AdminStockDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketPriceService {

    private final AdminMarketClient adminMarketClient;

    public BigDecimal getCurrentPrice(String symbol, BigDecimal fallbackPrice) {
        try {
            AdminStockDTO stock = adminMarketClient.getStock(symbol.toUpperCase());
            if (stock != null && stock.getCurrentPrice() != null && stock.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0) {
                return stock.getCurrentPrice();
            }
        } catch (Exception ex) {
            log.warn("Falling back to average price for {} due to market lookup issue: {}", symbol, ex.getMessage());
        }
        return fallbackPrice;
    }
}
