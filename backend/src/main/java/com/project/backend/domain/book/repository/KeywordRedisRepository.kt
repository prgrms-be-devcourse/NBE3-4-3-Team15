package com.project.backend.domain.book.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

/**
 * -- Redis기반 검색어 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 3월 03일 --
 */
@Repository
class KeywordRedisRepository(private val redisTemplate: RedisTemplate<String, String>) {

    private val KEY = "search_keywords"

    /**
     * -- 해당검색어가 존재하는지 판단하는 메서드 --
     *
     * @param -- keyword 검색어 --
     * @return -- Boolean 응답 --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    fun existsByKeyword(keyword: String): Boolean {
        return redisTemplate.opsForSet().isMember(KEY, keyword) ?: false
    }

    /**
     * -- 키워드를 redis에 저장하는 메서드 --
     *
     * @param -- keyword 검색어 --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    fun saveKeyword(keyword: String) {
        redisTemplate.opsForSet().add(KEY, keyword)
    }
}