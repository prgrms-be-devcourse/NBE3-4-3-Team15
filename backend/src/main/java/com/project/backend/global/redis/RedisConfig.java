package com.project.backend.global.redis;

import com.project.backend.global.redis.service.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * redis Configuration
 * @author 이광석
 * @since 25.02.26
 */
@Configuration
public class RedisConfig {


    /**
     * Redis 서버와의 연결을 생성하고 관리
     * @return LettuceConnectionFactory
     *
     *  @author 이광석
     *  @since 25.02.26
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    /**
     * Redis 서버와 상호작용을 위한 객체
     * RedisTemplate는 Redis에서 데이터를 저장하거나 읽어 올때 사용되는 주요 인터페이스 객체
     * template에 들어올 key,value 데이터를 String으로 직렬화 하여 저장
     * @param redisConnectionFactory
     * @return template
     *
     *  @author 이광석
     *  @since 25.02.26
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory  redisConnectionFactory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    /**
     * redis Pub/Sub 메시지를 수신하는 리스너
     * 특정 체널(notification 으로 시작하는 채널) 메시지를 구독하고, 수신된 메시지를 처리
     *
     * @param connectionFactory - Redis 연결 팩토리
     * @param listenerAdapter - Redis 메시지를 처리하는 이스너 어댑터
     * @return RedisMessageListenerContainer
     * @author 이광석
     * @since 25.02.26
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter,new PatternTopic("notification*"));
        return container;
    }

    /**
     * Redis 메시지를 처리하는 리스너 어댑터
     * RedisSubscriber 의 onMessage() 메서드와 연결
     * @param redisSubscriber
     * @return MessageListenerAdapter
     * @author 이광석
     * @since 25.03.09
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        return new MessageListenerAdapter(redisSubscriber, "onMessage");
    }
}
