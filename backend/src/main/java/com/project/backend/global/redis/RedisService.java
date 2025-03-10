package com.project.backend.global.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/**
 * Redis 데이터 저장,삭제, 검색 Service
 *
 * @author 이광석
 * @since 25.02.26
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 데이터 저장
     * @param key
     * @param value
     *
     *@author 이광석
     *@since 25.02.26
     */
    public void saveData(String key,String value){
        redisTemplate.opsForValue().set(key,value);

    }

    /**
     * Redis 데이터 검색
     * @param key
     * @return value
     *
     *@author 이광석
     *@since 25.02.26
     */
    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis 데이터 삭제
     * @param key
     *
     * @author 이광석
     * @since 25.02.26
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
