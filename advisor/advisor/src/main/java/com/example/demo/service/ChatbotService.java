package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;

@Service
public class ChatbotService {

    private final ChatMessageRepository repository;

    public ChatbotService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage saveQA(ChatMessage message) {
        return repository.save(message);
    }

    public String getAnswer(String question) {
        return repository.findByQuestionIgnoreCase(question)
                .map(ChatMessage::getAnswer)
                .orElse("Sorry, I don't know this answer.");
    }
}
