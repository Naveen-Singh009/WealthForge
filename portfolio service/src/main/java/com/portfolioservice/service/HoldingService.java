package com.portfolioservice.service;

import com.portfolioservice.dto.HoldingResponse;
import com.portfolioservice.model.Holding;
import com.portfolioservice.repository.HoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final MarketPriceService marketPriceService;

    //Get all holdings for a portfolio enriched with current price and P&L.

    public List<HoldingResponse> getHoldingsForPortfolio(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HoldingResponse mapToResponse(Holding h) {
        BigDecimal currentPrice = marketPriceService.getCurrentPrice(h.getAssetSymbol(), h.getAveragePrice());
        BigDecimal currentValue = currentPrice.multiply(h.getQuantity()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal investedValue = h.getAveragePrice().multiply(h.getQuantity()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal profitLoss = currentValue.subtract(investedValue);

        return HoldingResponse.builder()
                .id(h.getId())
                .assetSymbol(h.getAssetSymbol())
                .assetType(h.getAssetType())
                .quantity(h.getQuantity())
                .averagePrice(h.getAveragePrice())
                .currentPrice(currentPrice)
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .build();
    }
}
