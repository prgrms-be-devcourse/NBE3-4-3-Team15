package com.project.backend.domain.chatbot.service;

import com.project.backend.domain.book.service.BookService;
import com.project.backend.domain.chatbot.dto.AnswerDTO;
import com.project.backend.domain.review.review.service.ReviewService;
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
    private final ReviewService reviewService;
    private final BookService bookService;

    @Autowired
    public ChatbotServiceTest(ChatbotService chatbotService, ReviewService reviewService, BookService bookService) {
        this.chatbotService = chatbotService;
        this.reviewService = reviewService;
        this.bookService = bookService;
    }

    /**
     * Perplexity api 테스트
     *
     * @author shjung
     * @since 25. 2. 27.
     */
    @Test
    public void test1(){
        AnswerDTO s = chatbotService.recommendBook("책 하나 추천해줘", 25);

        System.out.println(s.getMessage());
    }
}