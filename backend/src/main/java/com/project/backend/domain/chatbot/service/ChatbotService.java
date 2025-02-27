package com.project.backend.domain.chatbot.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
     * @param message
     * @return
     *
     * @author shjung
     * @since 25. 2. 25.
     */
    public Map<Object, Object> generate(String message){
        return Map.of("generation", this.chatModel.call(message));
    }

    public Flux<ChatResponse> generateStream(String message){
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }

}
