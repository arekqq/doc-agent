package com.example.doc_agent.chat.service;

import com.example.doc_agent.ai.service.AiDocService;
import com.example.doc_agent.chat.dto.ChatRequest;
import com.example.doc_agent.file.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final FileService fileService;
    private final AiDocService aiDocService;

    public ChatService(FileService fileService, AiDocService aiDocService) {
        this.fileService = fileService;
        this.aiDocService = aiDocService;
    }

    public String getAnswer(ChatRequest request) {
        String filePath = fileService.getFilePath(request.documentId());
        return aiDocService.chat(filePath, request.question());
    }
}
