package com.project.backend.domain.chatbot.controller;

import com.project.backend.domain.chatbot.dto.AnswerDTO;
import com.project.backend.domain.chatbot.dto.QuestionDTO;
import com.project.backend.domain.chatbot.service.ChatbotService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shjung
 * @since 25. 2. 20.
 */
@RestController
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ResponseEntity<GenericResponse<AnswerDTO>> getAnswer(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                QuestionDTO message) {


        return ResponseEntity.ok(null);
    }
}
