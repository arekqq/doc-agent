package com.example.doc_agent.ai.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfiguration {

    @Bean
    ChatMemory chatMemory(Tokenizer tokenizer) {
        return TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }
}
