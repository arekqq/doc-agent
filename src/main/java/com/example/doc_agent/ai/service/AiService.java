package com.example.doc_agent.ai.service;


import dev.langchain4j.rag.query.Query;

@dev.langchain4j.service.spring.AiService
public interface AiService {

    String answer(Query query);
}
