package com.project.backend.domain.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChallengeErrorCode {

    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE_NOT_FOUND", "챌린지를 찾을 수 없습니다."),
    DUPLICATE_CHALLENGE(HttpStatus.CONFLICT, "DUPLICATE_CHALLENGE", "이미 해당 챌린지가 존재합니다."),
    DUPLICATE_ENTRY(HttpStatus.CONFLICT, "DUPLICATE_ENTRY", "이미 참여한 챌린지 입니다."),
    INVALID_CHALLENGE_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_CHALLENGE_CONTENT", "챌린지 내용이 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND","해당 맴버를 찾을 수 없습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","입력한 데이터가 유효하지 않습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN,"UNAUTHORIZED_ACCESS","권한이 없습니다"),
    DAILY_VERIFICATION(HttpStatus.BAD_REQUEST, "DAILY_VERIFICATION", "챌린지 인증 조건을 충족하지 못했습니다."),
    ALREADY_VALID(HttpStatus.CONFLICT, "ALREADY_VALID", "이미 인증된 챌린지 입니다."),
    ENTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "ENTRY_NOT_FOUND","참여 정보를 찾을 수 없습니다."),
    CANCEL_IMPOSSIBLE(HttpStatus.BAD_REQUEST, "CANCEL_IMPOSSIBLE", "챌린지 취소가 불가능합니다."),
    CREATE_CHALLENGE(HttpStatus.UNAUTHORIZED, "CREATE_CHALLENGE", "관리자만 접근 가능합니다.");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ChallengeErrorCode(HttpStatus status, String errorCode, String message){
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
