package com.example.demo.client;

import java.util.List;

import com.example.demo.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.StockDTO;
import com.example.demo.dto.UpdateQuantity;

@FeignClient(name = "admin-service", url = "http://localhost:8083", configuration = FeignClientConfig.class)
public interface AdminClient {

    // Companies
    @GetMapping("/api/admin/companies")
    List<CompanyDTO> getAllCompanies();

    // Stocks
    @GetMapping("/api/admin/stocks")
    List<StockDTO> getAllStocks();

    @GetMapping("/api/admin/stocks/{symbol}")
    StockDTO getStock(@PathVariable("symbol") String symbol);

    @PutMapping("/api/admin/stocks/update-quantity-buy")
    StockDTO updateQuantityBuy(@RequestBody UpdateQuantity request);

    @PutMapping("/api/admin/stocks/update-quantity-sell")
    StockDTO updateQuantitySell(@RequestBody UpdateQuantity request);
}