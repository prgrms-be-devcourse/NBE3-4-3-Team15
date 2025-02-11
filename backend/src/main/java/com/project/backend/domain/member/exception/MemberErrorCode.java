package com.project.backend.domain.member.exception;

import org.springframework.http.HttpStatus;

/**
 * MemberErrorCode
 * Member Controller에서 발생하는 예외 코드를 정의하는 Enum 클래스
 * @author 손진영
 * @since 2025.01.31
 */
public enum MemberErrorCode {

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "두 비밀번호가 일치하지 않습니다."),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "PASSWORD_LENGTH", "비밀번호는 8자리 이상이어야 합니다."),
    EXISTING_USERNAME(HttpStatus.CONFLICT, "EXISTING_USERNAME", "이미 존재하는 아이디 입니다."),
    NON_EXISTING_USERNAME(HttpStatus.NOT_FOUND, "NON_EXISTING_USERNAME", "존재하지 않는 사용자 입니다."),
    NON_EXISTING_USERID(HttpStatus.NOT_FOUND,"NON_EXISTING_USERiD","존재하지 않는 사용자 입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "INCORRECT_PASSWORD", "비밀번호가 맞지 않습니다."),
    NO_AUTHORIZED(HttpStatus.UNAUTHORIZED, "NO_AUTHORIZED", "인증정보가 없습니다."),
    INCORRECT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "INCORRECT_AUTHORIZED", "인증정보가 올바르지 않습니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "SAME_AS_OLD_PASSWORD", "새 비밀번호는 기존 비밀번호와 달라야 합니다."),
    EXISTING_EMAIL(HttpStatus.CONFLICT, "EXISTING_EMAIL","이미 존재하는 이메일입니다.");

    final HttpStatus status;
    final String code;
    final String message;

    MemberErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
