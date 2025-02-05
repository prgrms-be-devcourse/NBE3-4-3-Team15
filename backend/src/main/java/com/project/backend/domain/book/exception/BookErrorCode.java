package com.project.backend.domain.book.exception;

import org.springframework.http.HttpStatus;

/**
 * BookErrorCode
 * Book Controller에서 발생하는 예외 코드를 정의하는 Enum 클래스
 * @author 정재익
 * @since 2025.01.31
 */
public enum BookErrorCode {

    QUERY_EMPTY(HttpStatus.BAD_REQUEST, "400-1", "검색어를 입력하지 않았습니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "404-1", "해당 책을 찾을 수 없습니다."),
    BOOK_DB_EMPTY(HttpStatus.NOT_FOUND, "404-2", "도서 데이터베이스가 비어있습니다."),
    NO_FAVORITE_BOOKS(HttpStatus.NOT_FOUND, "404-3", "찜한 책이 없습니다.");

    final HttpStatus status;
    final String code;
    final String message;

    BookErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}