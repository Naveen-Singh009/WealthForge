package com.example.demo.service;

import com.example.demo.client.AdminClient;
import com.example.demo.client.AdvisorClient;
import com.example.demo.client.NotificationClient;
import com.example.demo.client.PortfolioClient;
import com.example.demo.dto.*;
import com.example.demo.entity.Holding;
import com.example.demo.entity.Investor;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HoldingRepository;
import com.example.demo.repository.InvestorRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvestorService {

    private final InvestorRepository repo;
    private final TransactionRepository tRepo;
    private final HoldingRepository hRepo;
    private final NotificationClient notificationClient;
    private final AdvisorClient advisorClient;
    private final AdminClient adminClient;
    private final PortfolioClient portfolioClient;

    @Transactional
    public String buyStock(Long authenticatedInvestorId, BuyRequestDTO dto) {
        validateInvestorOwnership(authenticatedInvestorId, dto.getInvestorId());

        Investor investor = repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        StockDTO stock = adminClient.getStock(dto.getSymbol());
        if (stock == null || stock.getCurrentPrice() == null) {
            throw new ResourceNotFoundException("Stock not found");
        }
        if (stock.getAvailableQuantity() == null || stock.getAvailableQuantity() < dto.getQuantity()) {
            throw new InvalidDataException("Insufficient stock quantity in market");
        }

        UpdateQuantity update = new UpdateQuantity();
        update.setSymbol(dto.getSymbol());
        update.setQuantity(dto.getQuantity());
        adminClient.updateQuantityBuy(update);

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

        StockDTO stock = adminClient.getStock(dto.getAssetName());
        if (stock == null || stock.getCurrentPrice() == null) {
            throw new ResourceNotFoundException("Stock not found");
        }

        UpdateQuantity update = new UpdateQuantity();
        update.setSymbol(dto.getAssetName());
        update.setQuantity(dto.getQuantity());
        adminClient.updateQuantitySell(update);

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

    @Transactional
    public String transferMoney(Long authenticatedInvestorId, TransferRequestDTO dto) {
        if (dto.getFromInvestorId() != null && !dto.getFromInvestorId().equals(authenticatedInvestorId)) {
            throw new InvalidDataException("You can transfer only from your own account");
        }

        Investor fromInvestor = repo.findById(authenticatedInvestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Investor toInvestor = repo.findById(dto.getToInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());
        if (fromInvestor.getBalance().compareTo(amount) < 0) {
            throw new InvalidDataException("Insufficient balance");
        }

        String date = LocalDate.now().toString();
        fromInvestor.setBalance(fromInvestor.getBalance().subtract(amount));
        toInvestor.setBalance(toInvestor.getBalance().add(amount));
        repo.save(fromInvestor);
        repo.save(toInvestor);

        Transaction senderTransaction = new Transaction();
        senderTransaction.setInvestorId(fromInvestor.getInvestorId());
        senderTransaction.setType("TRANSFER_DEBIT");
        senderTransaction.setAssetName("Fund Transfer");
        senderTransaction.setQuantity(0);
        senderTransaction.setPrice(amount);
        senderTransaction.setDate(date);
        tRepo.save(senderTransaction);

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setInvestorId(toInvestor.getInvestorId());
        receiverTransaction.setType("TRANSFER_CREDIT");
        receiverTransaction.setAssetName("Fund Transfer");
        receiverTransaction.setQuantity(0);
        receiverTransaction.setPrice(amount);
        receiverTransaction.setDate(date);
        tRepo.save(receiverTransaction);

        sendNotificationSafe(fromInvestor.getEmail(), "Fund transfer debit of $" + amount + " completed.");
        sendNotificationSafe(toInvestor.getEmail(), "Fund transfer credit of $" + amount + " received.");

        return "Transfer successful";
    }

    public List<AdvisorDTO> getAdvisorList() {
        return advisorClient.getAdvisors();
    }

    public List<Transaction> history(Long id) {
        return tRepo.findByInvestorId(id);
    }

    public List<Holding> getHolding(Long id) {
        return hRepo.findByInvestorId(id);
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
