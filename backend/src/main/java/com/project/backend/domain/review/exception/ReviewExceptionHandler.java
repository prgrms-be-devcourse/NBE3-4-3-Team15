package com.project.backend.domain.review.exception;


import com.project.backend.global.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.project.backend.domain.review")
public class ReviewExceptionHandler {

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<GenericResponse<String>> handleReviewException(ReviewException ex) {
        return ResponseEntity.status(ex.getStatus()).body(
                GenericResponse.of(
                        ex.getErrorCode(),  // 에러 코드 반환
                        ex.getMessage()     // 사용자에게 보여줄 메시지 반환
                )
        );
    }
}
