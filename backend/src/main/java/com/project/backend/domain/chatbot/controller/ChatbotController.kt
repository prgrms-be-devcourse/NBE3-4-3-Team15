package com.project.backend.domain.chatbot.controller

import com.project.backend.domain.chatbot.dto.AnswerDTO
import com.project.backend.domain.chatbot.service.ChatbotService
import com.project.backend.domain.member.service.MemberService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author shjung
 * @since 25. 2. 20.
 */
@Tag(name = "ChatbotController", description = "책 추천 컨트롤러")
@RestController
@RequestMapping("/chatbot")
@SecurityRequirement(name = "bearerAuth")
class ChatbotController(private val memberService: MemberService, private val chatbotService: ChatbotService) {

    @GetMapping
    @Operation(summary = "책 추천")
    fun getAnswer(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        message: String
    ): ResponseEntity<GenericResponse<AnswerDTO>> {
        val member = memberService.getMyProfile(userDetails.username)

        return ResponseEntity.ok(
            GenericResponse.of(
                chatbotService!!.recommendBook(message, member.id)
            )
        )
    }
}
