package com.project.backend.domain.review.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "리뷰 코멘트를 찾을 수 없습니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT, "DUPLICATE_REVIEW", "이미 해당 리뷰가 존재합니다."),
    INVALID_REVIEW_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_REVIEW_CONTENT", "리뷰 내용이 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND","해당 맴버를 찾을 수 없습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","입력한 데이터가 유효하지 않습니다"),
    INVALID_COMMENT_DEPTH(HttpStatus.BAD_REQUEST,"INVALID_COMMENT_DEPTH","대댓글에 댓글을 달수 없습니다.");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ReviewErrorCode(HttpStatus status, String errorCode, String message){
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
