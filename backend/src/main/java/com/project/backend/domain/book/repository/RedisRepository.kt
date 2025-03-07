package com.project.backend.domain.book.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

/**
 * -- Redis 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 3월 04일 --
 */
@Repository
class RedisRepository(private val redisTemplate: RedisTemplate<String, String>) {

    private val KEYWORDS_KEY = "search_keywords"
    private val HASH_KEY = "best_seller_hash"

    /**
     * -- 해당검색어가 존재하는지 판단하는 메서드 --
     *
     * @param -- keyword 검색어 --
     * @return -- Boolean 응답 --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    fun existKeyword(keyword: String): Boolean {
        return redisTemplate.opsForSet().isMember(KEYWORDS_KEY, keyword) ?: false
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
        redisTemplate.opsForSet().add(KEYWORDS_KEY, keyword)
    }

    /**
     * -- 사이트와 해시값을 저장하는 메소드 --
     *
     * @param -- hash 저장할 해시 값 --
     *
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun saveHash(hash: String) {
        redisTemplate.opsForValue().set(HASH_KEY, hash)
    }

    /**
     * -- 저장된 해시값을 반환하는 메소드 --
     *
     * @return 저장된 해시값, 없을 경우 빈 문자열 반환
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun loadPreviousHash(): String {
        return redisTemplate.opsForValue().get(HASH_KEY) ?: ""
    }
}
