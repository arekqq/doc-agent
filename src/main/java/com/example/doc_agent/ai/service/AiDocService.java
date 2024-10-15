package com.example.doc_agent.ai.service;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.stereotype.Service;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Service
public class AiDocService {

    private final EmbeddingModel embeddingModel;
    private final ContentRetriever contentRetriever;
    private final ChatLanguageModel chatLanguageModel;


    public AiDocService(EmbeddingModel embeddingModel,
                        ContentRetriever contentRetriever,
                        ChatLanguageModel chatLanguageModel) {
        this.embeddingModel = embeddingModel;
        this.contentRetriever = contentRetriever;
        this.chatLanguageModel = chatLanguageModel;
    }

    public String chat(String filePath, String question) {

        Document document = loadDocument(filePath, new TextDocumentParser());

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(
            100, 0, new OpenAiTokenizer(GPT_4_O_MINI));
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .documentSplitter(documentSplitter)
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();
        ingestor.ingest(document);


        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
            .contentRetriever(contentRetriever)
            .chatLanguageModel(chatLanguageModel)
            .build();

        String answer = chain.execute(question);
        System.out.println(answer);
        return answer;
    }
}
