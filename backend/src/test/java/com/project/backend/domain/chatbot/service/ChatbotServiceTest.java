package com.project.backend.domain.chatbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    /**
     * Perplexity api 테스트
     *
     * @author shjung
     * @since 25. 2. 27.
     */
    @Test
    public void test1(){
        Map<Object, Object> map = chatbotService.generate("동화책 하나 추천해줘 답변은 30자 이내로 해줘");

        List<Object> list = new ArrayList<>(map.keySet());

        for(Object key : list){
            System.out.println("key: " + key);  // key 값
            System.out.println(map.get(key));   // 메시지 결과 값
        }
    }

}