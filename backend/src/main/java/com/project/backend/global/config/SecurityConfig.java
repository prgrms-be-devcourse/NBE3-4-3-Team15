package com.project.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 스프링 시큐리티 설정 (SecurityFilterChain)
 * CSRF 비활성화 (JWT 사용을 위한 설정)
 * Form 로그인 비활성화 (JWT 인증을 사용하기 위해)
 * 세션 사용 X -> STATELESS 모드 설정 (JWT 방식)
 * CORS 설정 적용 (corsConfig() 사용)
 * 엔드포인트별 접근 권한 설정
 *
 * @author 이원재
 * @since 25. 2. 3.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfig())) // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        // GET 요청은 모두 허용(책 목록, 리뷰 조회 등)
                        .requestMatchers(HttpMethod.GET, "/book/list", "/book/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/{reviewId}/comments").permitAll()

                        // 로그인 및 회원가입은 인증 없이 허용
                        .requestMatchers("/members/login", "/members").permitAll()

                        // 그 외 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * CORS 설정
     * 프론트엔드에서 API 요청 시 CORS 문제 방지
     * 특정 출처에서 요청 허용
     * 모든 헤더 허용
     * Authorization 헤더 노출 허용(JWT 인증 사용 시 필요)
     * @return CORS 설정이 적용된 CorsConfigurationSource 객체
     * @author 이원재
     * @since 25. 2. 3.
     */

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 클라이언트가 쿠키 및 인증 정보 전송 허용
        configuration.addAllowedOrigin("http://localhost:3000"); // 허용할 프론트엔드 주소
        configuration.addAllowedHeader("*"); // 모든 요청 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용 (GET, POST 등)
        configuration.addExposedHeader("Authorization"); // 클라이언트에서 Authorization 헤더 접근 가능

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}