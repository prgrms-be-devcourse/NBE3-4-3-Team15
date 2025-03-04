package com.project.backend.domain.challenge.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChallengeErrorCode {

    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "챌린지를 찾을 수 없습니다."),
    DUPLICATE_CHALLENGE(HttpStatus.CONFLICT, "DUPLICATE_REVIEW", "이미 해당 챌린지가 존재합니다."),
    INVALID_CHALLENGE_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_REVIEW_CONTENT", "챌린지 내용이 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND","해당 맴버를 찾을 수 없습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","입력한 데이터가 유효하지 않습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN,"UNAUTHORIZED_ACCESS","권한이 없습니다");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ChallengeErrorCode(HttpStatus status, String errorCode, String message){
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
