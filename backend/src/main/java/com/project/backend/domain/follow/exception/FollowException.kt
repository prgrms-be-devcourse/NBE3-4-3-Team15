package com.project.backend.domain.follow.exception;

import org.springframework.http.HttpStatus;

class FollowException(
    private val followErrorCode: FollowErrorCode
) : RuntimeException(followErrorCode.message) {

    fun getStatus(): HttpStatus {
        return followErrorCode.status
    }

    fun getCode(): String {
        return followErrorCode.code
    }
}
