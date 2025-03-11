package com.project.backend.global.sse.service;


import com.project.backend.global.sse.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


/**
 * sse 관련 service
 */
@Service
@RequiredArgsConstructor
public class SseService {
    private final EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 600L *1000*60;

    /**
     * SSE 연결 메소드
     * SSEEmiter 생성 및 SSE 연결을 관리
     * @param memberId
     * @return emitter
     *
     * @author 이광석
     * @since 25.02.23
     */
    public SseEmitter subscribeSse(Long memberId){
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(memberId,emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));


        try{
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
        }catch (IOException e){
            emitterRepository.deleteById(memberId);
        }

        return emitter;
    }


    /**
     * 프론트로 알람 전달 메소드
     *
     * @param message
     *
     * @author 이광석
     * @since 25.02.23
     */
    public void broadcastNotification(String message) {
        emitterRepository.getAllEmitters().forEach((memberId, emitter) -> {
           try{
               emitter.send(SseEmitter.event()
                       .name(memberId+"")
                       .data(message));
           }catch (IOException e){
               emitterRepository.deleteById(memberId);
           }
        });
    }

    /**
     * 메시지를 특정 사용자에게 전달하는 메서드
     * @param memberId - 수신자 memberId
     * @param message - 메시지 내용
     * @author 이광석
     * @since 25.03.09
     */
    public void sendNotificationToUser(Long memberId,String message) {
        SseEmitter emitter = emitterRepository.findById(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                emitterRepository.deleteById(memberId);
            }
        }
    }

}
