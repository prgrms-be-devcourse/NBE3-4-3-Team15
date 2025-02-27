package com.project.backend.domain.chatbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Test
    public void test1(){
        Map<Object, Object> map = chatbotService.generate("동화책 하나 추천해줘");

        List<Object> list = new ArrayList<>(map.keySet());

        for(Object key : list){
            System.out.println("key: " + key);
            System.out.println(map.get(key));
        }
    }

    @Test
    public void test2(){
        Flux<ChatResponse> flux = chatbotService.generateStream("동화책 하나 추천해줘");

        System.out.println(flux.collectList().block());
    }

}