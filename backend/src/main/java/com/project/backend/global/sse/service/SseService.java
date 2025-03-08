package com.project.backend.global.sse.service;


import com.project.backend.global.redis.RedisService;
import com.project.backend.global.sse.rapository.EmitterRepository;
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
    private final RedisService redisService;

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
            String key = "SSE_CONNECT:"+memberId;
            String value = "server-1";

            if(redisService.getData(key)!=null){
                redisService.deleteData(key);

            }

            redisService.saveData(key,value,DEFAULT_TIMEOUT);

            SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

            try{
                emitter.send(SseEmitter.event()
                        .name("connect")
                        .data("SSE 연결 성공"));
            }catch (IOException e){
                emitter.completeWithError(e);
            }





//        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
//
//        emitterRepository.save(memberId,emitter);
//
//        emitter.onCompletion(()->emitterRepository.deleteBy(memberId)); // emitter
//
//        emitter.onTimeout(()->emitterRepository.deleteBy(memberId));
//
//
//
//        try{
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("SSE 연결 성공"));
//            System.out.println("sse 연결 성공");
//        }catch (IOException e){
//            emitterRepository.deleteBy(memberId);
//            emitter.completeWithError(e);
//        }
//

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
//        SseEmitter emitter = emitterRepository.findById(memberId);
          if(redisService.getData("SSE_CONNECT:"+memberId)==null){
              System.out.println("없음");
              return;
          }
          SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
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
