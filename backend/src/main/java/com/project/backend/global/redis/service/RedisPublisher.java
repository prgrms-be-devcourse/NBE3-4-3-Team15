package com.project.backend.global.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * RedisPublisher
 * Redis에 데이터를 송신하는 서비스
 *
 * author 이광석
 * since 25.03.09
 */
@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final StringRedisTemplate redisTemplate;


    /**
     * 특정 유저에게 알림 메시지를 전송
     * Redis의 Pub/Sub을 이용하여 "notification:{memberId}" 채널로 메시지를 발행
     * @param memberId - 알람을 받을 유저
     * @param message - 알람 내용
     *
     * @author 이광석
     * @since 25.03.09
     */
    public void publishToUser(Long memberId,String message){
        redisTemplate.convertAndSend("notification:"+memberId,message);
    }
}
