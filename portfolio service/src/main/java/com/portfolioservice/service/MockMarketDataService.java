package com.portfolioservice.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


 //Mock implementation of a market data service.
 //Returns static/hardcoded prices per asset symbol.
 //Replace with a real HTTP client to a Market Data Service later
@Service
public class MockMarketDataService {

    private static final Map<String, BigDecimal> PRICES = Map.ofEntries(
            // Stocks
            Map.entry("AAPL", new BigDecimal("178.50")),
            Map.entry("GOOGL", new BigDecimal("141.25")),
            Map.entry("MSFT", new BigDecimal("415.60")),
            Map.entry("AMZN", new BigDecimal("185.30")),
            Map.entry("TSLA", new BigDecimal("245.80")),
            Map.entry("META", new BigDecimal("505.75")),
            Map.entry("NVDA", new BigDecimal("880.40")),

            // Crypto
            Map.entry("BTC", new BigDecimal("65000.00")),
            Map.entry("ETH", new BigDecimal("3500.00")),

            // Bonds / ETFs
            Map.entry("SPY", new BigDecimal("510.20")),
            Map.entry("QQQ", new BigDecimal("440.15")));

    
    public BigDecimal getPrice(String symbol) {
        return PRICES.getOrDefault(symbol.toUpperCase(), BigDecimal.ONE);
    }
}
