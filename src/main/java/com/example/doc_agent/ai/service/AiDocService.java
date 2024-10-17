package com.example.doc_agent.ai.service;

import com.example.doc_agent.chat.dto.ChatRequest;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.Filter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static java.util.stream.Collectors.joining;

@Service
public class AiDocService {

    private final EmbeddingStoreIngestor ingestor;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatLanguageModel chatLanguageModel;

    public AiDocService(EmbeddingStoreIngestor ingestor,
                        EmbeddingModel embeddingModel,
                        EmbeddingStore<TextSegment> embeddingStore,
                        ChatLanguageModel chatLanguageModel) {
        this.ingestor = ingestor;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.chatLanguageModel = chatLanguageModel;
    }

    public void ingest(Document document) {
        ingestor.ingest(document);
    }

    public String chat(ChatRequest request) {
        Filter idFilter = metadataKey("id").isEqualTo(request.documentId());
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .filter(idFilter)
            .build();
        Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(chatLanguageModel)
            .contentRetriever(contentRetriever)
            .build();
        return assistant.answer(request.question());
    }

    public String chatLowLevel(ChatRequest request) {

        Embedding questionEmbedding = embeddingModel.embed(request.question()).content();
        int maxResults = 3;
        double minScore = 0.7;
        List<EmbeddingMatch<TextSegment>> search = embeddingStore.search(EmbeddingSearchRequest.builder()
            .queryEmbedding(questionEmbedding)
            .maxResults(maxResults)
            .minScore(minScore)
            .filter(metadataKey("id").isEqualTo(request.documentId()))
            .build()).matches();
        PromptTemplate promptTemplate = PromptTemplate.from(
            """
                Answer the following question to the best of your ability:
                Question:
                {{question}}
                Base your answer on the following information:
                {{information}}
                If information doesn't cover the question - answer literally:
                ====== not enough information ======
                """);
        String information = search.stream()
            .map(match -> match.embedded().text())
            .collect(joining("\n\n"));
        Map<String, Object> variables = Map.ofEntries(
            Map.entry("question", request.question()),
            Map.entry("information", information)
        );
        Prompt prompt = promptTemplate.apply(variables);
        AiMessage aiMessage = chatLanguageModel.generate(prompt.toUserMessage()).content();
        return aiMessage.text();
    }
}
