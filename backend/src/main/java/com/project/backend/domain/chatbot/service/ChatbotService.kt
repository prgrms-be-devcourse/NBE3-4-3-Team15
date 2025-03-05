package com.project.backend.domain.chatbot.service

import com.project.backend.domain.book.service.BookService
import com.project.backend.domain.chatbot.dto.AnswerDTO
import com.project.backend.domain.review.review.service.ReviewService
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author shjung
 * @since 25. 2. 20.
 */
@Service
class ChatbotService @Autowired constructor(
    private val chatModel: OpenAiChatModel,
    private val reviewService: ReviewService,
    private val bookService: BookService
) {
    /**
     *
     * 책 추천받기 위한 AI api 호출 함수
     *
     * @param message 책 추천 받기 위한 정보
     * @param userId 유저 ID
     * @return
     *
     * @author shjung
     * @since 25. 2. 25.
     */
    fun generate(message: String, userId: Long): AnswerDTO {
        val bookIds = reviewService.getBookIds(userId)
        val bookTitles = bookService.searchBookTitlesByIds(bookIds)

        val prompt = makeRecommendPrompt(message, bookTitles)

        return AnswerDTO(chatModel.call(prompt).replace("[*]".toRegex(), ""))
    }

    /**
     *
     * 책 정보 물어볼 프롬프트 생성하는 함수
     *
     * @param message 현재 질문
     * @param list 유저가 리뷰한 책 정보 (현재 3개. 토큰 절약을 위함)
     *
     * @author
     * @since 25. 3. 05.
     */
    private fun makeRecommendPrompt(message: String, list: List<String>): String {
        val sb = StringBuilder(message)
        sb.append("\n위와 관련된 책을 추천해. 너는 책을 많이 읽어본 전문가야. 책을 추천할 때 책의 제목와 저자, 추천 이유를 알려줘. 길이 30자에 맞춰서 작성해. 그리고 추천할 책의 개수는 3개야. \n")
        if (list.isNotEmpty()) {
            sb.append("\n지금 내가 관심있게 읽은 책은 아래와 같아. 관심분야가 비슷한 책으로 추천해. \n")
            sb.append(list.toString())
        }
        sb.append("형식은 다음과 같이 해 줘. \n")
        sb.append("""
                책: ,
                저자: ,
                추천 이유:
                """)
        return sb.toString()
    }
}
