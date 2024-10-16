package com.example.doc_agent.ai.service;

@dev.langchain4j.service.spring.AiService
public interface AiService {

    String answer(String userMessage);
}
