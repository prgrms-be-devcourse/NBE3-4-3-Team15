package com.project.backend.domain.book.util

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * --Redis 설정 클래스--
 *
 * @author -- 정재익 --
 * @since -- 3월 3일 --
 */
@Configuration
class RedisConfig {

    /**
     * --Redis 설정 메서드 --
     * redis의 key과 value의 직렬화 방식을 문자열로 설정
     *
     * @param -- redisConnectionFactory redis 연결 인터페이스 --
     * @return -- RedisTemplate<String, String> redis에 저장,조회를 하는 템플릿 --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        return RedisTemplate<String, String>().apply {
            connectionFactory = redisConnectionFactory
            valueSerializer = StringRedisSerializer()
            keySerializer = StringRedisSerializer()
        }
    }
}
