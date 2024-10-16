package com.example.doc_agent.ai.config;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreIngestorConfiguration {

    @Bean
    EmbeddingStoreIngestor embeddingStoreIngestor(
        EmbeddingModel embeddingModel,
        EmbeddingStore<TextSegment> embeddingStore
    ) {
        return EmbeddingStoreIngestor.builder()
            .documentSplitter(DocumentSplitters.recursive(500, 50))
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();
    }
}
