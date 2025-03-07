package com.project.backend.domain.chatbot.controller

import com.project.backend.domain.chatbot.dto.AnswerDTO
import com.project.backend.domain.chatbot.dto.QuestionDTO
import com.project.backend.domain.chatbot.service.ChatbotService
import com.project.backend.domain.member.service.MemberService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author shjung
 * @since 25. 2. 20.
 */
@RestController
@RequestMapping("/chatbot")
class ChatbotController {
    private val chatbotService: ChatbotService? = null
    private val memberService: MemberService? = null

    @GetMapping
    fun getAnswer(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        message: QuestionDTO
    ): ResponseEntity<GenericResponse<AnswerDTO>> {
        val member = memberService!!.getMyProfile(userDetails.username)

        return ResponseEntity.ok(
            GenericResponse.of(
                chatbotService!!.recommendBook(message.question, member.id)
            )
        )
    }
}
