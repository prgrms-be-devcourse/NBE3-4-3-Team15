package com.project.backend.domain.book.service

import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis

/**
 * -- Redis 사이트 캐시용 클래스 --
 * 사이트의 html 구조를 캐싱한 것을 저장하고 있다
 *
 * @author -- 정재익 --
 * @since -- 3월 02일 --
 */
@Component
class RedisHash {
    private val jedis = Jedis("localhost", 6379)
    private val HASH_KEY = "best_seller_hash"

    /**
     * -- 사이트와 해시값을 저장하는 메소드 --
     *
     * @param -- hash 저장할 해시 값 --
     *
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun saveHash(hash: String) {
        jedis.set(HASH_KEY, hash)
    }

    /**
     * -- 저장된 해시값을 반환하는 메소드 --
     *
     * @return 저장된 해시값, 없을 경우 빈 문자열 반환
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun loadPreviousHash(): String {
        return jedis.get(HASH_KEY) ?: ""
    }
}