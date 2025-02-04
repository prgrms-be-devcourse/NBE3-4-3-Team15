package com.project.backend.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * -- JWT 유틸리티 클래스 --
 * JWT 토큰 생성, 유효성 검증 및 사용자 정보 추출을 담당하는 클래스
 *
 * @author 이원재
 * @since 25. 2. 4.
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey; // JWT 서명에 사용할 비밀 키 (application-secret.yml 에서 설정)

    private final long accessTokenValidTime = 1000L * 60 * 30; // 액세스 토큰 유효 시간 (30분)

    /**
     * username 을 기반으로 JWT 토큰을 생성
     *
     * @param username
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) // 발생 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime)) // 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명 방식과 키 설정
                .compact(); // 토큰 생성
    }

    /**
     * JWT 토큰의 유효성을 검증
     *
     * @param token JWT 토큰
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱
            return true; // 예외가 발생하지 않으면 유효한 토큰
        } catch (Exception e) {
            return false; // 예외가 발생하면 유효하지 않은 토큰
        }
    }

    /**
     * JWT 토큰에서 username을 추출
     *
     * @param token JWT 토큰
     * @return 토큰에서 추출한 username
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody()
                .getSubject(); // username 반환
    }

    /**
     * 비밀 키를 바이트 배열로 변환하여 서명 키로 사용
     *
     * @return 서명 키
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // BASE64로 디코딩하여 비밀 키 추출
        return Keys.hmacShaKeyFor(keyBytes); // HMAC SHA 키 생성
    }

}

// 브랜치 연동 테스트
