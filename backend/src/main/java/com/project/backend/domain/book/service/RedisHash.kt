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

    /**
     * -- 사이트와 해시값을 저장하는 메소드 --
     *
     * @param --url 사이트 주소 --
     * @param -- 해시 값 --
     *
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun saveHash(url: String, hash: String) {
        jedis.set(url, hash)
    }

    /**
     * -- 저장된 해시값을 반환하는 메소드 --
     *
     * @param --url 사이트 주소 --
     * @return --String 해시 값 --
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun loadPreviousHash(url: String): String {
        return jedis.get(url)
    }
}