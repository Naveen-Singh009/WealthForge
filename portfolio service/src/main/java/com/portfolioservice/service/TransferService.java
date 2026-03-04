package com.portfolioservice.service;

import com.portfolioservice.dto.TransferRequest;
import com.portfolioservice.exception.InsufficientBalanceException;
import com.portfolioservice.model.Portfolio;
import com.portfolioservice.model.Transaction;
import com.portfolioservice.model.TransactionType;
import com.portfolioservice.repository.PortfolioRepository;
import com.portfolioservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final PortfolioService portfolioService;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    //Transfer funds between two portfolios belonging to the same investor.
     //The operation is fully transactional — both sides succeed or neither does
    @Transactional
    public void transferFunds(Long investorId, TransferRequest request) {
        if (request.getFromPortfolioId().equals(request.getToPortfolioId())) {
            throw new IllegalArgumentException("Source and destination portfolios must be different");
        }

        Portfolio source = portfolioService.getPortfolioEntity(request.getFromPortfolioId());
        Portfolio destination = portfolioService.getPortfolioEntity(request.getToPortfolioId());

        // Verify the investor owns both portfolios
        if (!source.getInvestorId().equals(investorId) || !destination.getInvestorId().equals(investorId)) {
            throw new IllegalArgumentException("Both portfolios must belong to the authenticated investor");
        }

        BigDecimal amount = request.getAmount();

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in portfolio " + source.getId()
                            + ". Available: " + source.getBalance() + ", Requested: " + amount);
        }

        // Debit source, credit destination
        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        portfolioRepository.save(source);
        portfolioRepository.save(destination);

        // Record transactions for audit trail
        transactionRepository.save(Transaction.builder()
                .portfolioId(source.getId())
                .type(TransactionType.TRANSFER)
                .amount(amount.negate())
                .assetSymbol(null)
                .build());

        transactionRepository.save(Transaction.builder()
                .portfolioId(destination.getId())
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .assetSymbol(null)
                .build());
    }
}
