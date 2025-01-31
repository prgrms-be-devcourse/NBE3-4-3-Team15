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
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "400-3", "비밀번호는 8자리 이상이어야 합니다."),
    EXISTING_ID(HttpStatus.CONFLICT, "409-1", "이미 존재하는 아이디 입니다."),
    NON_EXISTING_ID(HttpStatus.NOT_FOUND, "404-1", "존재하지 않는 사용자 입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "401-1", "비밀번호가 맞지 않습니다."),
    NO_AUTHORIZED(HttpStatus.UNAUTHORIZED, "401-2", "인증정보가 없습니다."),
    INCORRECT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "401-3", "인증정보가 올바르지 않습니다.");
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
