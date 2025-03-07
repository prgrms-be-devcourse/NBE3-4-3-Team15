package com.project.backend.global.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

/**
 * --RestTemplate 설정 클래스--
 *
 * @author -- 정재익 --
 * @since -- 3월 4일 --
 */
@Configuration
class RestTemplateConfig {

    /**
     * --RestTemplate 설정 클래스--
     * SimpleClientHttpRequestFactory를 포함하여 Bean 생성
     *
     * @author -- 정재익 --
     * @since -- 3월 4일 --
     */
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate(SimpleClientHttpRequestFactory())
    }
}
