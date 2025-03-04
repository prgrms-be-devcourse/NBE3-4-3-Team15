package com.project.backend.global.sse.service;


import com.project.backend.global.sse.rapository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseService {
    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 600L *1000*60;

    /**
     * SSE 연결 메소드
     * @param memberId
     * @return emitter
     *
     * @author 이광석
     * @since 25.02.23
     */
    public SseEmitter subscribeSse(Long memberId){

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(memberId,emitter);

        emitter.onCompletion(()->emitterRepository.deleteBy(memberId));

        emitter.onTimeout(()->emitterRepository.deleteBy(memberId));

        try{
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
            System.out.println("sse 연결 성공");
        }catch (IOException e){
            emitterRepository.deleteBy(memberId);
            emitter.completeWithError(e);
        }


        return emitter;
    }


    /**
     * 프론트로 알람 전달 메소드
     * @param memberId
     * @param message
     *
     * @author 이광석
     * @since 25.02.23
     */
    public void sendNotification(Long memberId,String message){
        SseEmitter emitter = emitterRepository.findById(memberId);
        if (emitter == null) {
            System.out.println("SSE Emitter not found for memberId: " + memberId);
        }
        System.out.println("sse 알람 시작");
        if(emitter !=null){
            try{
                System.out.println("알람 전달 성공");
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            }catch(IOException e){
                System.out.println("알람 전달 실패");
                emitterRepository.deleteBy(memberId);
                emitter.completeWithError(e);
            }
        }
    }


}
