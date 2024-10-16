package com.example.doc_agent.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.stereotype.Service;

@Service
public class AiDocService {

    private final EmbeddingStoreIngestor ingestor;
    private final AiService aiService;

    public AiDocService(EmbeddingStoreIngestor ingestor, AiService aiService) {
        this.ingestor = ingestor;
        this.aiService = aiService;
    }

    public void ingest(Document document) {
        ingestor.ingest(document);
    }

    public String chat(String question) {
        return aiService.answer(question);
    }
}
