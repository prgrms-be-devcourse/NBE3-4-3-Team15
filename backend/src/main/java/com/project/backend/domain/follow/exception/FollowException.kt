package com.project.backend.domain.follow.exception;

import org.springframework.http.HttpStatus;

public class FollowException extends RuntimeException {
    private final FollowErrorCode followErrorCode;

    public FollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode.message);
        this.followErrorCode = followErrorCode;
    }

    public HttpStatus getStatus() {
        return followErrorCode.status;
    }

    public String getCode() {
        return followErrorCode.code;
    }
}
