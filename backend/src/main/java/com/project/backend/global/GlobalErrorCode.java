package com.project.backend.global;

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
    NOT_VALID(HttpStatus.BAD_REQUEST, "400-1", "요청이 올바르지 않습니다."),;

    final HttpStatus status;
    final String code;
    final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
