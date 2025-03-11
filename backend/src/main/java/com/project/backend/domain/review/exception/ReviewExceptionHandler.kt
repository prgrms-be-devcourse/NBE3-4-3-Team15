package com.project.backend.domain.review.exception

import com.project.backend.global.response.GenericResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice(basePackages = ["com.project.backend.domain.review"])
class ReviewExceptionHandler{
    @ExceptionHandler(ReviewException::class)
    fun handleReviewException(ex:ReviewException): ResponseEntity<GenericResponse<String>> {
        return ResponseEntity
            .status(ex.status)
            .body(GenericResponse.of(ex.errorCode,ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<GenericResponse<String>> {
        val errorMessage = ex.fieldError?.defaultMessage ?: "유효성 검사 실패"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(GenericResponse.of("VALIDATION_FAILED", errorMessage))
    }
}