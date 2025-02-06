package com.project.backend.domain.follow.exception;

import org.springframework.http.HttpStatus;

/**
 * 팔로우 관련 예외 코드 Enum 클래스
 * author 이원재
 * since 2025.01.27
 */
public enum FollowErrorCode {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "NOT_FOUND_MEMBER", "존재하지 않는 회원입니다."),
    CAN_NOT_FOLLOW_MYSELF(HttpStatus.BAD_REQUEST, "CAN_NOT_FOLLOW_MYSELF", "자기 자신을 팔로우할 수 없습니다.");

    final HttpStatus status;
    final String code;
    final String message;

    FollowErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
