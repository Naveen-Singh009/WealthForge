package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ChatMessage;

public interface ChatMessageRepository 
extends JpaRepository<ChatMessage, Long> {

Optional<ChatMessage> findByQuestionIgnoreCase(String question);
}
