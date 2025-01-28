package com.project.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * 애플리케이션 전역에서 발생하는 예외 코드를 정의하는 Enum 클래스
 * author 이원재
 * since 2025.01.27
 */
@Getter
public enum GlobalErrorCode {
    NOT_VALID(HttpStatus.BAD_REQUEST, "400-1", "요청이 올바르지 않습니다."),
    /**
     * 멤버 예외 코드
     * author 손진영
     * since 2025.01.28
     */
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "400-2", "비밀번호가 잘못되었습니다."),
    EXISTING_ID(HttpStatus.UNAUTHORIZED, "409-1", "이미 존재하는 아이디 입니다.");

    final HttpStatus status;
    final String code;
    final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
