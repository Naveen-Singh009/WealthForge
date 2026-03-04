package com.example.demo;

import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.service.ChatbotService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private ChatMessageRepository repository;

    @InjectMocks
    private ChatbotService service;

    @Test
    void testGetAnswer_WhenQuestionExists() {

        // Arrange
        ChatMessage msg = new ChatMessage();
        msg.setQuestion("What is SIP?");
        msg.setAnswer("Systematic Investment Plan");

        when(repository.findByQuestionIgnoreCase("What is SIP?"))
                .thenReturn(Optional.of(msg));

        // Act
        String answer = service.getAnswer("What is SIP?");

        // Assert
        assertEquals("Systematic Investment Plan", answer);
        verify(repository, times(1))
                .findByQuestionIgnoreCase("What is SIP?");
    }

    @Test
    void testGetAnswer_WhenQuestionNotExists() {

        // Arrange
        when(repository.findByQuestionIgnoreCase("Unknown"))
                .thenReturn(Optional.empty());

        // Act
        String answer = service.getAnswer("Unknown");

        // Assert
        assertEquals("Sorry, I don't know this answer.", answer);
    }
}