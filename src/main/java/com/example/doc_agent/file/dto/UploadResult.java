package com.example.doc_agent.file.dto;

public record UploadResult(
    UploadFileStatus status,
    String id) {
}
