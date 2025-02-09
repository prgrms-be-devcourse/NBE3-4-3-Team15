package com.project.backend.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * -- JWT 인증 필터 --
 * 요청이 들어올 때마다 JWT 토큰을 확인하여 유효한 토큰인 경우 인증을 설정한다.
 * 인증이 성공하면 SecurityContext에 사용자 정보를 설정하여 후속 작업에서 사용할 수 있도록 한다.
 *
 * @author 이원재
 * @since 25. 2. 4.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthentizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * HTTP 요청에 대해 JWT 인증을 처리하는 메서드
     * 요청 헤더에서 JWT 토큰을 추출하고, 유효성 검증 후 인증 정보를 설정한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 요청 처리 중 예외 발생 시
     * @throws IOException IO 오류 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 토큰 추출
        String token = getTokenFromRequest(request);

        // 토큰이 존재하고 유효하다면, 사용자 정보를 SecurityContext에 설정
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // SecurityContext 에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 필터 체인 실행(다음 필터로 넘어감)
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * 1. Authorization 헤더에서 Bearer <token> 형식으로 추출
     * 2. 쿠키에서 accessToken 추출
     *
     * @param request HTTP 요청
     * @return 토큰 문자열, 없으면 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 Bearer <token> 형식으로 추출
        String bearerToken = request.getHeader("Authorization");
        // Bearer <token> 형식의 토큰만 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 쿠키에서 accessToken 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}