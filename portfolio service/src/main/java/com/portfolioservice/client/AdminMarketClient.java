package com.portfolioservice.client;

import com.portfolioservice.config.FeignClientConfig;
import com.portfolioservice.dto.AdminStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "admin-service", url = "http://localhost:8083", configuration = FeignClientConfig.class)
public interface AdminMarketClient {

    @GetMapping("/api/admin/stocks/{symbol}")
    AdminStockDTO getStock(@PathVariable("symbol") String symbol);
}
