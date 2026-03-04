package com.example.demo.client;

import java.util.List;

import com.example.demo.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.AdvisorDTO;

@FeignClient(name = "advisor", url = "http://localhost:8084", configuration = FeignClientConfig.class)
public interface AdvisorClient {

    @GetMapping("/api/advisor/list/all")
    List<AdvisorDTO> getAdvisors();

    @GetMapping("/api/advisor/chatbot/ask")
    String getAdvice(@RequestParam("question") String question);
}