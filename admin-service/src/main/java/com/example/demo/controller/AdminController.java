package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.InvestorDTO;
import com.example.demo.entity.Company;
import com.example.demo.entity.Stock;
import com.example.demo.entity.UpdatePriceDTO;
import com.example.demo.entity.UpdateQuantity;
import com.example.demo.service.AdminService;
import com.example.demo.service.StockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;
    private final StockService stockService;

    // ================= COMPANY APIs =================

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> listCompanies() {
        return ResponseEntity.ok(service.listCompanies());
    }

    @GetMapping("/companiesList")
    public ResponseEntity<List<Company>> companiesList() {
        return ResponseEntity.ok(service.listCompanies());
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> addCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok(service.addCompany(company));
    }

    @PostMapping("/add")
    public ResponseEntity<Company> add(@Valid @RequestBody Company company) {
        return ResponseEntity.ok(service.addCompany(company));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id,
                                                 @Valid @RequestBody Company company) {
        return ResponseEntity.ok(service.updateCompany(id, company));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Company> update(@PathVariable Long id,
                                          @Valid @RequestBody Company company) {
        return ResponseEntity.ok(service.updateCompany(id, company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {
        service.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }


    // ================= INVESTOR APIs =================

    @GetMapping("/investors")
    public ResponseEntity<List<InvestorDTO>> listInvestors() {
        return ResponseEntity.ok(service.listInvestors());
    }

    @GetMapping("/investorsList")
    public ResponseEntity<List<InvestorDTO>> investorsList() {
        return ResponseEntity.ok(service.listInvestors());
    }


    // ================= STOCK APIs =================

    @PostMapping("/stocks")
    public ResponseEntity<String> addStock(@Valid @RequestBody Stock stock){
        return ResponseEntity.ok(stockService.addStock(stock));
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.findStocksBySymbol(symbol));
    }

    @GetMapping("/stocks/price/{symbol}")
    public ResponseEntity<Double> getPrice(@PathVariable String symbol) {
        Stock stock = stockService.getStock(symbol);
        return ResponseEntity.ok(stock.getCurrentPrice().doubleValue());
    }

    @PutMapping("/stocks/update-price")
    public ResponseEntity<Stock> updatePrice(@Valid @RequestBody UpdatePriceDTO request) {
        Stock updated = stockService.updateStockPrice(
                request.getSymbol(),
                request.getPrice()
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/stocks/update-quantity-buy")
    public ResponseEntity<Stock> updateQuantityBuy(@Valid @RequestBody UpdateQuantity request){
        Stock updated = stockService.updateQuantityBuy(request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/stocks/update-quantity-sell")
    public ResponseEntity<Stock> updateQuantitySell(@Valid @RequestBody UpdateQuantity request){
        Stock updated = stockService.updateQuantitySell(request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/stocks/id/{id}")
    public ResponseEntity<String> deleteStockById(@PathVariable Long id){
        stockService.deleteStockById(id);
        return ResponseEntity.ok("Stock deleted successfully");
    }

    @DeleteMapping("/stocks/symbol/{symbol}")
    public ResponseEntity<String> deleteStockBySymbol(@PathVariable String symbol){
        stockService.deleteStock(symbol);
        return ResponseEntity.ok("Stock deleted successfully");
    }

}
