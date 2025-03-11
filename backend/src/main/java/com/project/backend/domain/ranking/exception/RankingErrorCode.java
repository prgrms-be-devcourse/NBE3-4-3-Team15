package com.project.backend.domain.ranking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RankingErrorCode {
    UNKNOWN_RANKING_TYPE(HttpStatus.BAD_REQUEST, "UNKNOWN_RANKING_TYPE", "알 수 없는 랭킹 타입입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    RankingErrorCode(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
