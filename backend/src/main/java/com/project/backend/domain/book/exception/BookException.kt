package com.project.backend.domain.book.exception

import org.springframework.http.HttpStatus

/**
 * BookException
 *
 * @author 정재익
 * @since 2025.01.31
 */
class BookException(bookErrorCode: BookErrorCode) : RuntimeException(bookErrorCode.message) {
    val status: HttpStatus = bookErrorCode.status
    val code: String = bookErrorCode.code
}
