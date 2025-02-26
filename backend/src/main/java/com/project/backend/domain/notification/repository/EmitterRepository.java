package com.project.backend.domain.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class EmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter findById(Long memberId) {
        return emitters.get(memberId);
    }

    public SseEmitter save(Long memberId,SseEmitter emitter){
        emitters.put(memberId,emitter);
        return emitters.get(memberId);
    }

    public void deleteBy(Long memberId){
        emitters.remove(memberId);
    }
}
