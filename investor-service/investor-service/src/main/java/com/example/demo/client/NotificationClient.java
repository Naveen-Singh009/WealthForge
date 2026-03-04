package com.example.demo.client;

import com.example.demo.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.NotificationRequest;

@FeignClient(name = "notification-service", url = "http://localhost:8087", configuration = FeignClientConfig.class)
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    String sendNotification(@RequestBody NotificationRequest request);
}