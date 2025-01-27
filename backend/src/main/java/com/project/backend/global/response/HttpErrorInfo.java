package com.project.backend.global.response;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * HttpErrorInfo
 * 에러 발생 시 클라이언트에게 전달할 에러 정보를 담는 클래스
 * @param code 에러 코드
 * @param path 요청 경로
 * @param message 에러 메시지
 * @param errorDetails 에러 상세 정보
 * @param timestamp 에러 발생 시간
 * author 이원재
 * since 2021.01.27
 */
public record HttpErrorInfo(String code,
                            String path,
                            String message,
                            List<ErrorDetail> errorDetails,
                            ZonedDateTime timestamp
) {
    public static HttpErrorInfo of(String code, String path, String message, List<ErrorDetail> errorDetails) {
        return new HttpErrorInfo(code, path, message, errorDetails, ZonedDateTime.now());
    }

}
