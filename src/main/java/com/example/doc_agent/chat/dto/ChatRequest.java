package com.example.doc_agent.chat.dto;

import java.util.UUID;

public record ChatRequest(
    UUID documentId,
    String question
) {
}
