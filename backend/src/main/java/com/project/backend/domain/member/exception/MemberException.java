package com.project.backend.domain.member.exception;

import org.springframework.http.HttpStatus;

/**
 * MemberException
 *
 * @author 손진영
 * @since 2025.01.31
 */
public class MemberException extends RuntimeException {
    private final MemberErrorCode memberErrorCode;

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.message);
        this.memberErrorCode = memberErrorCode;
    }

    public HttpStatus getStatus() {
        return memberErrorCode.status;
    }

    public String getCode() {
        return memberErrorCode.code;
    }
}
