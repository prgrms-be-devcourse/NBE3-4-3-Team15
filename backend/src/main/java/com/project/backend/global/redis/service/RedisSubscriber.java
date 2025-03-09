package com.project.backend.global.redis.service;


import com.project.backend.global.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.Message;

import java.util.List;


/**
 * redissubscriber
 * 레디스에서 메시지를 수신한다.
 *
 * @author 이광석
 * @since 25.03.09
 */
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SseService sseService;

    /**
     * Redis 에 데이터가 추가되면 받고 sse로 클라이언트로 전달
     *
     * @param message  - Redis에서 발행된 메시지 (실제 데이터)
     * @param pattern - 메시지가 발행된 Redis 채널명
     *
     * @author 이광석
     * @since 25.03.09
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String msg = message.toString();

        String[] list = channel.split(":");


        if(list.length<2) {
            System.out.println("sub 전체 알림" + channel + msg );
            sseService.broadcastNotification(msg); // 모든 SSE 연결에 전송
        }else{
            Long memberId = Long.parseLong(channel.split(":")[1]);
            System.out.println("sub user"+channel+memberId+msg);
            sseService.sendNotificationToUser(memberId,msg);
        }
    }

}