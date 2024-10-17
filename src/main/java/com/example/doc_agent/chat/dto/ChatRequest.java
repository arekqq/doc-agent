package com.example.doc_agent.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChatRequest(
    @NotNull UUID documentId,
    @NotEmpty String question
) {
}
