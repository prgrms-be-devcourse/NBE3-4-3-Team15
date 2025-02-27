package com.project.backend.global.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


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
}
