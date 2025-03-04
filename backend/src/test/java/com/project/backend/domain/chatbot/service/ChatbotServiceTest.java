package com.project.backend.domain.chatbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author shjung
 * @since 25. 2. 24.
 */
@SpringBootTest
class ChatbotServiceTest {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotServiceTest(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * Perplexity api 테스트
     *
     * @author shjung
     * @since 25. 2. 27.
     */
    @Test
    public void test1(){
        String s = chatbotService.generate("동화책 하나 추천해줘", 1L);

        System.out.println(s);
    }

}