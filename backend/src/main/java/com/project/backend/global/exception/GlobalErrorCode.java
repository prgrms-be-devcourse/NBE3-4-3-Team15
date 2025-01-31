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
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "400-2", "잘못된 정렬 기준입니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "404-1", "해당 책을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404-2", "해당 멤버를 찾을 수 없습니다."),
    BOOK_DB_EMPTY(HttpStatus.NOT_FOUND, "404-3", "도서 데이터베이스가 비어있습니다."),
    NO_FAVORITE_BOOKS(HttpStatus.NOT_FOUND, "404-4", "찜한 책이 없습니다.");


    final HttpStatus status;
    final String code;
    final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
