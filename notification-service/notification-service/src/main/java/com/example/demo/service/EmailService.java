package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String email, String message) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(email);
        mail.setSubject("Stock Notification");
        mail.setText(message);

        mailSender.send(mail);

    }
}