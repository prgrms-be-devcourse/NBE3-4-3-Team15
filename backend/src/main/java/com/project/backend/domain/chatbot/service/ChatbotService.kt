package com.project.backend.domain.chatbot.service

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
    private val reviewService: ReviewService
) {
    /**
     *
     * @param message
     * @param userId
     * @return
     *
     * @author shjung
     * @since 25. 2. 25.
     */
    fun generate(message: String, userId: Long): AnswerDTO {
        // TODO. User ID로 조회하는 로직으로 변경 후 개발 진행
        //List<ReviewsDTO> reviews = reviewService.getUserReviews(null);

        val list: List<String> = ArrayList()

        val prompt = makePrompt(message, list)

        return AnswerDTO(chatModel.call(prompt).replace("[*]".toRegex(), ""))
    }

    private fun makePrompt(message: String, list: List<String>): String {
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
