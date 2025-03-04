package com.project.backend.domain.chatbot.service;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author shjung
 * @since 25. 2. 20.
 */
@Service
public class ChatbotService {

    private final OpenAiChatModel chatModel;

    @Autowired
    public ChatbotService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     *
     * Perplexity api
     *
     * @param message
     * @return
     *
     * @author shjung
     * @since 25. 2. 25.
     */
    public Map<Object, Object> generate(String message){
        // TODO. 1. DB로 연결해서 하는 로직 추가

        // TODO. 2. 프롬프트 추가


        return Map.of("generation", this.chatModel.call(message));
    }

}
