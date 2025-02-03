package com.project.backend.global.advice;

import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.global.exception.GlobalErrorCode;
import com.project.backend.global.response.ErrorDetail;
import com.project.backend.global.response.HttpErrorInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * GlobalControllerAdvice
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 클래스
 * author 이원재
 * since 2025.01.27
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    /**
     * Validation 예외 발생 시 처리하는 핸들러
     * @param ex 발생한 예외
     * @param request HttpServletRequest
     * @return {@link HttpErrorInfo} 에러 정보
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<HttpErrorInfo> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ErrorDetail> errors = new ArrayList<>();
        GlobalErrorCode globalErrorCode = GlobalErrorCode.NOT_VALID;

        //Field 에러 처리
        for (FieldError error : bindingResult.getFieldErrors()) {
            ErrorDetail customError = ErrorDetail.of(error.getField(), error.getDefaultMessage());

            errors.add(customError);
        }

        //Object 에러 처리
        for (ObjectError globalError : bindingResult.getGlobalErrors()) {
            ErrorDetail customError = ErrorDetail.of(
                    globalError.getObjectName(),
                    globalError.getDefaultMessage()
            );

            errors.add(customError);
        }

        return ResponseEntity.status(ex.getStatusCode().value())
                .body(HttpErrorInfo.of(
                        GlobalErrorCode.NOT_VALID.getCode(),
                        request.getRequestURI(),
                        GlobalErrorCode.NOT_VALID.getMessage(),
                        errors)
                );

    }

    /**
     * MemberException 발생 시 처리하는 핸들러
     * @param ex 발생한 예외
     * @param request HttpServletRequest
     * @return {@link HttpErrorInfo} 에러 정보
     * @author 손진영
     * @since 2025.01.28
     */
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<HttpErrorInfo> handleGlobalException(
            MemberException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatus())
                .body(HttpErrorInfo.of(
                        ex.getCode(),
                        request.getRequestURI(),
                        ex.getMessage()
                        )
                );
    }

    /**
     * BookException 발생 시 처리하는 핸들러
     * @param ex 발생한 예외
     * @param request HttpServletRequest
     * @return {@link HttpErrorInfo} 에러 정보
     * @author 정재익
     * @since 2025.01.31
     */
    @ExceptionHandler(BookException.class)
    public ResponseEntity<HttpErrorInfo> handleGlobalException(
            BookException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatus())
                .body(HttpErrorInfo.of(
                                ex.getCode(),
                                request.getRequestURI(),
                                ex.getMessage()
                        )
                );
    }
}