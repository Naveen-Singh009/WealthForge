package com.portfolioservice.service;

import com.portfolioservice.dto.TransactionResponse;
import com.portfolioservice.model.Transaction;
import com.portfolioservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final PortfolioService portfolioService;
    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> getPortfolioTransactions(Long investorId, Long portfolioId) {
        portfolioService.getPortfolioEntityForInvestor(portfolioId, investorId);
        return transactionRepository.findByPortfolioIdOrderByTimestampDesc(portfolioId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAllTransactionsForInvestor(Long investorId) {
        List<Long> portfolioIds = portfolioService.findByInvestorId(investorId)
                .stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());

        if (portfolioIds.isEmpty()) {
            return Collections.emptyList();
        }

        return transactionRepository.findByPortfolioIdInOrderByTimestampDesc(portfolioIds)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .portfolioId(tx.getPortfolioId())
                .type(tx.getType())
                .assetSymbol(tx.getAssetSymbol())
                .amount(tx.getAmount())
                .timestamp(tx.getTimestamp())
                .build();
    }
}
