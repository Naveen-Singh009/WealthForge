package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ChatMessage;
import com.example.demo.service.ChatbotService;

@RestController
@RequestMapping("/api/advisor/chatbot")
public class ChatbotController {

    private final ChatbotService service;

    public ChatbotController(ChatbotService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ChatMessage addQA(@RequestBody ChatMessage message) {
        return service.saveQA(message);
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return service.getAnswer(question);
    }
}