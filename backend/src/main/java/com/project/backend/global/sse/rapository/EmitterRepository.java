package com.project.backend.global.sse.rapository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * EmitterRepository
 *
 * sse 구독(emitter)을 관리한다.
 */
@Slf4j
@Repository
public class EmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * ssemitter 반환
     * @param memberId
     * @return emitter
     */
    public SseEmitter findById(Long memberId) {
        return emitters.get(memberId);
    }

    /**
     * ssemitter 추가(저장)
     * @param memberId
     * @param emitter
     * @return emitter
     */
    public SseEmitter save(Long memberId,SseEmitter emitter){
        emitters.put(memberId,emitter);
        return emitters.get(memberId);
    }

    /**
     * ssemitter 삭제
     * 구독 해제
     * @param memberId
     */
    public void deleteBy(Long memberId){
        emitters.remove(memberId);
    }

    public Map<Long, SseEmitter> getAllEmitters() {
        return emitters;
    }
}
