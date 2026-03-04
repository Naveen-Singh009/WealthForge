package com.authservice.service;

import com.authservice.dto.InvestorProfileSyncRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvestorProfileSyncService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${investor.service.base-url:http://localhost:8085}")
    private String investorServiceBaseUrl;

    @Value("${app.internal.registration-key:WEALTHFORGE_INTERNAL_KEY}")
    private String internalRegistrationKey;

    public void createInvestorProfile(Long investorId, String name, String email, BigDecimal initialBalance) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Key", internalRegistrationKey);

        InvestorProfileSyncRequest payload = new InvestorProfileSyncRequest(
                investorId,
                name,
                email,
                initialBalance);

        HttpEntity<InvestorProfileSyncRequest> requestEntity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(
                    investorServiceBaseUrl + "/api/investor/internal/register",
                    requestEntity,
                    Void.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Failed to sync investor profile to investor-service", ex);
        }
    }
}
