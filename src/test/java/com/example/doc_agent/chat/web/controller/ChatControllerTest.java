package com.example.doc_agent.chat.web.controller;

import com.example.doc_agent.chat.dto.ChatRequest;
import com.example.doc_agent.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void chat_ShouldReturnAnswer_WhenRequestIsValid() throws Exception {
        // Given
        when(chatService.getAnswer(any(ChatRequest.class))).thenReturn("Mocked Answer");
        String jsonRequest = "{\"message\":\"Hello\"}";

        // When + Then
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"answer\":\"Mocked Answer\"}"));
        verify(chatService, times(1)).getAnswer(any(ChatRequest.class));
    }

    @Test
    void chat_ShouldHandleEmptyRequestBody() throws Exception {
        // Given
        when(chatService.getAnswer(any(ChatRequest.class))).thenReturn("");

        // When + Then
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))  // Empty request body
            .andExpect(status().isOk())
            .andExpect(content().json("{\"answer\":\"\"}"));
        verify(chatService, times(1)).getAnswer(any(ChatRequest.class));
    }
}
