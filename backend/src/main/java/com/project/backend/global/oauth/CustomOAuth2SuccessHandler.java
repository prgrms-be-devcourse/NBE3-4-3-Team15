package com.project.backend.global.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 클라이언트에 반환하는 핸들러 클래스
 *
 * @author 손진영
 * @since 25. 02. 07.
 */
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 인증 성공 시 JWT 토큰을 생성하여 클라이언트에 반환합니다.
     *
     * @param request       클라이언트 요청 정보
     * @param response      서버 응답 정보
     * @param authentication 인증 객체
     * @throws IOException  입출력 예외
     *
     * @author 손진영
     * @since 25. 02. 07.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        response.addHeader("Set-Cookie", "accessToken=" + token + "; HttpOnly; Path=/; Max-Age=3600; SameSite=Strict");

        response.sendRedirect("http://localhost:3000/");
    }
}
