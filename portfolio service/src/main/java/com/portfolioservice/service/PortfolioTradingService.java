package com.portfolioservice.service;

import com.portfolioservice.dto.TradeRequest;
import com.portfolioservice.exception.InsufficientBalanceException;
import com.portfolioservice.model.Holding;
import com.portfolioservice.model.Portfolio;
import com.portfolioservice.model.Transaction;
import com.portfolioservice.model.TransactionType;
import com.portfolioservice.repository.HoldingRepository;
import com.portfolioservice.repository.PortfolioRepository;
import com.portfolioservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PortfolioTradingService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

    private final PortfolioService portfolioService;
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void buyAsset(Long investorId, Long portfolioId, TradeRequest request) {
        Portfolio portfolio = portfolioService.getPortfolioEntityForInvestor(portfolioId, investorId);

        String symbol = request.getAssetSymbol().toUpperCase();
        BigDecimal quantity = scale(request.getQuantity());
        BigDecimal price = scale(request.getPrice());
        BigDecimal totalCost = scale(price.multiply(quantity));

        if (portfolio.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in portfolio " + portfolioId + ". Available: "
                            + portfolio.getBalance() + ", Required: " + totalCost);
        }

        Holding holding = holdingRepository.findByPortfolioIdAndAssetSymbol(portfolioId, symbol)
                .orElseGet(() -> Holding.builder()
                        .portfolioId(portfolioId)
                        .assetSymbol(symbol)
                        .assetType(request.getAssetType() == null || request.getAssetType().isBlank()
                                ? "STOCK"
                                : request.getAssetType())
                        .quantity(ZERO)
                        .averagePrice(price)
                        .build());

        BigDecimal existingQty = holding.getQuantity();
        BigDecimal newQty = scale(existingQty.add(quantity));
        BigDecimal newAvgPrice;
        if (existingQty.compareTo(BigDecimal.ZERO) == 0) {
            newAvgPrice = price;
        } else {
            BigDecimal weightedTotal = holding.getAveragePrice().multiply(existingQty).add(price.multiply(quantity));
            newAvgPrice = scale(weightedTotal.divide(newQty, 6, RoundingMode.HALF_UP));
        }

        holding.setQuantity(newQty);
        holding.setAveragePrice(newAvgPrice);

        portfolio.setBalance(scale(portfolio.getBalance().subtract(totalCost)));
        portfolioRepository.save(portfolio);
        holdingRepository.save(holding);

        transactionRepository.save(Transaction.builder()
                .portfolioId(portfolioId)
                .type(TransactionType.BUY)
                .assetSymbol(symbol)
                .amount(totalCost)
                .build());
    }

    @Transactional
    public void sellAsset(Long investorId, Long portfolioId, TradeRequest request) {
        Portfolio portfolio = portfolioService.getPortfolioEntityForInvestor(portfolioId, investorId);

        String symbol = request.getAssetSymbol().toUpperCase();
        BigDecimal quantity = scale(request.getQuantity());
        BigDecimal price = scale(request.getPrice());

        Holding holding = holdingRepository.findByPortfolioIdAndAssetSymbol(portfolioId, symbol)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No holding found for symbol " + symbol + " in portfolio " + portfolioId));

        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient quantity for symbol " + symbol
                    + ". Available: " + holding.getQuantity() + ", Requested: " + quantity);
        }

        BigDecimal proceeds = scale(price.multiply(quantity));
        BigDecimal remainingQty = scale(holding.getQuantity().subtract(quantity));

        if (remainingQty.compareTo(BigDecimal.ZERO) == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(remainingQty);
            holdingRepository.save(holding);
        }

        portfolio.setBalance(scale(portfolio.getBalance().add(proceeds)));
        portfolioRepository.save(portfolio);

        transactionRepository.save(Transaction.builder()
                .portfolioId(portfolioId)
                .type(TransactionType.SELL)
                .assetSymbol(symbol)
                .amount(proceeds)
                .build());
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(6, RoundingMode.HALF_UP);
    }
}
