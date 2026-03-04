package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Notification;
import com.example.demo.service.EmailService;
import com.example.demo.service.WebSocketService;

@RestController
@RequestMapping({"/notification", "/api/notifications"})
public class NotificationController {

    @Autowired
    EmailService emailService;

    @Autowired
    WebSocketService webSocketService;


    @PostMapping("/send")
    public String sendNotification(@RequestBody Notification notification) {

        emailService.sendEmail(
                notification.getEmail(),
                notification.getMessage()
        );

        webSocketService.sendNotification(
                notification.getMessage()
        );

        return "Notification Sent Successfully";

    }

}
