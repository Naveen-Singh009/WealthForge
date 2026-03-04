package com.example.demo.service;

import com.example.demo.client.AdminClient;
import com.example.demo.client.AdvisorClient;
import com.example.demo.client.NotificationClient;
import com.example.demo.client.PortfolioClient;
import com.example.demo.dto.*;
import com.example.demo.entity.Investor;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvestorService {

    private static final BigDecimal DEFAULT_INITIAL_BALANCE = BigDecimal.ZERO;

    private final InvestorRepository repo;
    private final NotificationClient notificationClient;
    private final AdvisorClient advisorClient;
    private final AdminClient adminClient;
    private final PortfolioClient portfolioClient;

    @Value("${app.internal.registration-key:WEALTHFORGE_INTERNAL_KEY}")
    private String internalRegistrationKey;

    @Transactional
    public String buyStock(Long authenticatedInvestorId, BuyRequestDTO dto) {
        validateInvestorOwnership(authenticatedInvestorId, dto.getInvestorId());

        Investor investor = repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        StockDTO stock;
        try {
            stock = adminClient.getStock(dto.getSymbol());
        } catch (Exception ex) {
            throw new InvalidDataException("Failed to read stock data from admin-service: " + ex.getMessage());
        }
        if (stock == null || stock.getCurrentPrice() == null) {
            throw new ResourceNotFoundException("Stock not found");
        }
        if (stock.getAvailableQuantity() == null || stock.getAvailableQuantity() < dto.getQuantity()) {
            throw new InvalidDataException("Insufficient stock quantity in market");
        }

        UpdateQuantity update = new UpdateQuantity();
        update.setSymbol(dto.getSymbol());
        update.setQuantity(dto.getQuantity());
        try {
            adminClient.updateQuantityBuy(update);
        } catch (Exception ex) {
            throw new InvalidDataException("Failed to reserve stock quantity in admin-service: " + ex.getMessage());
        }

        PortfolioTradeRequest tradeRequest = new PortfolioTradeRequest();
        tradeRequest.setAssetSymbol(dto.getSymbol());
        tradeRequest.setQuantity(BigDecimal.valueOf(dto.getQuantity()));
        tradeRequest.setPrice(stock.getCurrentPrice());
        tradeRequest.setAssetType("STOCK");

        try {
            portfolioClient.buyInPortfolio(dto.getPortfolioId(), tradeRequest);
        } catch (Exception ex) {
            rollbackAdminQuantitySell(update);
            throw new InvalidDataException("Buy failed in portfolio service: " + ex.getMessage());
        }

        BigDecimal total = stock.getCurrentPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        String message = String.format("""
                Subject: Transaction Confirmation: BUY

                Dear %s,

                Your buy order was executed successfully.
                Portfolio ID: %d
                Asset: %s
                Price: $%.2f
                Quantity: %d
                Total: $%.2f
                Date: %s
                """,
                investor.getInvestorName(),
                dto.getPortfolioId(),
                dto.getSymbol(),
                stock.getCurrentPrice().doubleValue(),
                dto.getQuantity(),
                total.doubleValue(),
                LocalDate.now());

        sendNotificationSafe(investor.getEmail(), message);
        return "Stock bought successfully in portfolio " + dto.getPortfolioId();
    }

    @Transactional
    public String sellStock(Long authenticatedInvestorId, SellRequestDTO dto) {
        validateInvestorOwnership(authenticatedInvestorId, dto.getInvestorId());

        Investor investor = repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        StockDTO stock;
        try {
            stock = adminClient.getStock(dto.getAssetName());
        } catch (Exception ex) {
            throw new InvalidDataException("Failed to read stock data from admin-service: " + ex.getMessage());
        }
        if (stock == null || stock.getCurrentPrice() == null) {
            throw new ResourceNotFoundException("Stock not found");
        }

        UpdateQuantity update = new UpdateQuantity();
        update.setSymbol(dto.getAssetName());
        update.setQuantity(dto.getQuantity());
        try {
            adminClient.updateQuantitySell(update);
        } catch (Exception ex) {
            throw new InvalidDataException("Failed to return stock quantity in admin-service: " + ex.getMessage());
        }

        PortfolioTradeRequest tradeRequest = new PortfolioTradeRequest();
        tradeRequest.setAssetSymbol(dto.getAssetName());
        tradeRequest.setQuantity(BigDecimal.valueOf(dto.getQuantity()));
        tradeRequest.setPrice(stock.getCurrentPrice());
        tradeRequest.setAssetType("STOCK");

        try {
            portfolioClient.sellInPortfolio(dto.getPortfolioId(), tradeRequest);
        } catch (Exception ex) {
            rollbackAdminQuantityBuy(update);
            throw new InvalidDataException("Sell failed in portfolio service: " + ex.getMessage());
        }

        BigDecimal total = stock.getCurrentPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        String message = String.format("""
                Subject: Transaction Confirmation: SELL

                Dear %s,

                Your sell order was executed successfully.
                Portfolio ID: %d
                Asset: %s
                Price: $%.2f
                Quantity: %d
                Total: $%.2f
                Date: %s
                """,
                investor.getInvestorName(),
                dto.getPortfolioId(),
                dto.getAssetName(),
                stock.getCurrentPrice().doubleValue(),
                dto.getQuantity(),
                total.doubleValue(),
                LocalDate.now());

        sendNotificationSafe(investor.getEmail(), message);
        return "Stock sold successfully in portfolio " + dto.getPortfolioId();
    }

    public String transferBetweenPortfolios(Long authenticatedInvestorId, PortfolioTransferRequest request) {
        try {
            Map<String, Object> body = responseBody(
                    portfolioClient.transferBetweenPortfolios(request),
                    "transfer funds between portfolios");
            return extractMessage(body, "Transfer completed successfully");
        } catch (Exception ex) {
            throw new InvalidDataException("Transfer failed: " + ex.getMessage());
        }
    }

    public List<AdvisorDTO> getAdvisorList() {
        return advisorClient.getAdvisors();
    }

    public String getAdvice(String question) {
        return advisorClient.getAdvice(question);
    }

    public List<CompanyDTO> getCompanyList() {
        return adminClient.getAllCompanies();
    }

    public List<StockDTO> getStockList() {
        return adminClient.getAllStocks();
    }

    public Map<String, Object> createPortfolio(PortfolioCreateRequest request) {
        return responseBody(portfolioClient.createPortfolio(request), "create portfolio");
    }

    public Map<String, Object> getMyPortfolios() {
        return responseBody(portfolioClient.getMyPortfolios(), "list portfolios");
    }

    public Map<String, Object> getPortfolioById(Long portfolioId) {
        return responseBody(portfolioClient.getPortfolioById(portfolioId), "get portfolio");
    }

    public Map<String, Object> getPortfolioHoldings(Long portfolioId) {
        return responseBody(portfolioClient.getHoldings(portfolioId), "get holdings");
    }

    public Map<String, Object> getPortfolioPerformance(Long portfolioId) {
        return responseBody(portfolioClient.getPerformance(portfolioId), "get performance");
    }

    public Map<String, Object> transferBetweenPortfolios(PortfolioTransferRequest request) {
        return responseBody(portfolioClient.transferBetweenPortfolios(request), "transfer between portfolios");
    }

    public Map<String, Object> getOverallPerformance() {
        return responseBody(portfolioClient.getOverallPerformance(), "get overall performance");
    }

    public Map<String, Object> getHistory(Long authenticatedInvestorId) {
        repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));
        return responseBody(portfolioClient.getAllTransactions(), "get transaction history");
    }

    public Map<String, Object> getPortfolioHistory(Long authenticatedInvestorId, Long portfolioId) {
        repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));
        return responseBody(portfolioClient.getTransactionsForPortfolio(portfolioId), "get portfolio transaction history");
    }

    public List<Investor> listAllInvestors() {
        return repo.findAll();
    }

    @Transactional
    public Investor registerInvestorProfile(String internalKey, InvestorRegistrationRequest request) {
        if (!Objects.equals(internalRegistrationKey, internalKey)) {
            throw new InvalidDataException("Invalid internal registration key");
        }

        Long investorId = request.getInvestorId();
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        repo.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getInvestorId().equals(investorId))
                .ifPresent(existing -> {
                    throw new InvalidDataException("Email is already linked to another investor");
                });

        Investor investor = repo.findById(investorId).orElseGet(Investor::new);
        boolean isNewInvestor = investor.getInvestorId() == null;

        investor.setInvestorId(investorId);
        investor.setInvestorName(request.getInvestorName().trim());
        investor.setEmail(normalizedEmail);

        if (isNewInvestor) {
            investor.setBalance(defaultInitialBalance(request.getInitialBalance()));
        } else if (investor.getBalance() == null) {
            investor.setBalance(defaultInitialBalance(request.getInitialBalance()));
        }

        return repo.save(investor);
    }

    private void validateInvestorOwnership(Long authenticatedInvestorId, Long requestInvestorId) {
        if (requestInvestorId != null && !requestInvestorId.equals(authenticatedInvestorId)) {
            throw new InvalidDataException("Investor ID in request does not match authenticated user");
        }
    }

    private void sendNotificationSafe(String email, String message) {
        if (email == null || email.isBlank()) {
            return;
        }
        try {
            NotificationRequest request = new NotificationRequest();
            request.setEmail(email);
            request.setMessage(message);
            notificationClient.sendNotification(request);
        } catch (Exception ex) {
            log.warn("Notification send failed: {}", ex.getMessage());
        }
    }

    private Map<String, Object> responseBody(ResponseEntity<Map<String, Object>> response, String action) {
        if (response == null || response.getBody() == null) {
            throw new InvalidDataException("Unable to " + action + ": empty response from portfolio-service");
        }
        return response.getBody();
    }

    private String extractMessage(Map<String, Object> body, String fallbackMessage) {
        Object message = body.get("message");
        return message instanceof String messageText ? messageText : fallbackMessage;
    }

    private BigDecimal defaultInitialBalance(BigDecimal requestedInitialBalance) {
        return requestedInitialBalance == null ? DEFAULT_INITIAL_BALANCE : requestedInitialBalance;
    }

    private void rollbackAdminQuantitySell(UpdateQuantity update) {
        try {
            adminClient.updateQuantitySell(update);
        } catch (Exception rollbackEx) {
            log.error("Failed to rollback admin quantity after buy failure: {}", rollbackEx.getMessage());
        }
    }

    private void rollbackAdminQuantityBuy(UpdateQuantity update) {
        try {
            adminClient.updateQuantityBuy(update);
        } catch (Exception rollbackEx) {
            log.error("Failed to rollback admin quantity after sell failure: {}", rollbackEx.getMessage());
        }
    }
}
