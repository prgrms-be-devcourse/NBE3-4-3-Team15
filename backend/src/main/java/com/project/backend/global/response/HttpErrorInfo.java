package com.project.backend.global.response;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * HttpErrorInfo
 * 에러 발생 시 클라이언트에게 전달할 에러 정보를 담는 클래스
 * @param code          에러 코드
 * @param path          요청 경로
 * @param message       에러 메시지
 * @param errorDetails  에러 상세 정보
 * @param timeStamp     에러 발생 시간
 * author 이원재
 * since 2025.01.27
 */
public record HttpErrorInfo(String code,
                            String path,
                            String message,
                            ZonedDateTime timeStamp,
                            List<ErrorDetail> errorDetails
) {
    /**
     * HttpErrorInfo 생성자
     * 필수 필드에 대해 유효성 검사 수행 후 객체 생성
     * @param code
     * @param path
     * @param message
     * @param timeStamp
     * @param errorDetails
     */
    public HttpErrorInfo(String code, String path, String message, ZonedDateTime timeStamp,
                         List<ErrorDetail> errorDetails) {
        this.code = code;
        this.path = path;
        this.message = message;
        this.timeStamp = timeStamp;
        this.errorDetails = errorDetails;
    }

    /**
     * HttpErrorInfo 생성 팩토리 메서드
     * 필드 에러 정보 포함 생성 메서드
     * @param code 커스텀 예외 코드
     * @param path 예외가 발생한 요청 경로
     * @param message 예외가 발생한 이유
     * @param errorDetails 필드 에러 정보
     * @return {@link HttpErrorInfo}
     * @author 이원재
     * since 2025.01.27
     */
    public static HttpErrorInfo of(String code, String path, String message, List<ErrorDetail> errorDetails) {
        return new HttpErrorInfo(code, path, message, null, errorDetails);
    }

    /**
     * HttpErrorInfo 생성 팩토리 메서드
     * 필드 에러 정보 제외 간단한 생성 메서드
     * @param code 커스텀 예외 코드
     * @param path 예외가 발생한 요청 경로
     * @param message 예외가 발생한 이유
     * @return {@link HttpErrorInfo}
     * @author 이원재
     * since 2025.01.27
     */
    public static HttpErrorInfo of(String code, String path, String message) {
        return of(code, path, message, null);
    }

}
