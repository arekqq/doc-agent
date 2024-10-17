package com.example.doc_agent.chat.web.controller;

import com.example.doc_agent.chat.dto.ChatRequest;
import com.example.doc_agent.chat.dto.ChatResponse;
import com.example.doc_agent.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody @Valid ChatRequest request) {
        String answer = chatService.getAnswer(request);
        return ResponseEntity.ok()
            .body(new ChatResponse(answer));
    }
}
