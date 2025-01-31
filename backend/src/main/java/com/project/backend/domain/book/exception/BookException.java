package com.project.backend.domain.book.exception;

import org.springframework.http.HttpStatus;

/**
 * BookException
 *
 * @author 정재익
 * @since 2025.01.31
 */
public class BookException extends RuntimeException{
    private final BookErrorCode bookErrorCode;

    public BookException(BookErrorCode bookErrorCode) {
        super(bookErrorCode.message);
        this.bookErrorCode = bookErrorCode;
    }

    public HttpStatus getStatus() {
        return bookErrorCode.status;
    }

    public String getCode() {
        return bookErrorCode.code;
    }
}
