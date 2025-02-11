package com.project.backend.domain.notification.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"NOTIFICATION_NOT_FOUND","알람을 찾을 수 없습니다");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    NotificationErrorCode(HttpStatus status, String errorCode, String message){
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
