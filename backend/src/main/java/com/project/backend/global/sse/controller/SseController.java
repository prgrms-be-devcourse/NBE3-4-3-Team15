package com.project.backend.global.sse.controller;

import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * Sse controller
 *
 * @author 이광석
 * @since 25.03.04
 */
@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;
    private final MemberService memberService;


    /**
     * Sse 연결
     * Sse 연결 테스트를 위해작성 실제로는 로그인시 바로 sse 연결
     * @param userDetails
     * @return ResponseEntity.ok(emitter)
     *
     * @author 이광석
     * @since 25.03.04
     */
    @GetMapping(value="/sse" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> SseConnect(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = memberService.getMyProfile(userDetails.getUsername()).getId();

        SseEmitter emitter = sseService.subscribeSse(memberId);

        return ResponseEntity.ok(emitter);
    }
}
