package com.project.backend.global.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {
    private final GlobalErrorCode globalErrorCode;

    public GlobalException(GlobalErrorCode globalErrorCode) {
        super(globalErrorCode.message);
        this.globalErrorCode = globalErrorCode;
    }

    public HttpStatus getStatus() {
        return globalErrorCode.status;
    }

    public String getCode() {
        return globalErrorCode.code;
    }
}
