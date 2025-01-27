package com.project.backend.global;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * GenericResponse
 * 요청이 성공했을 때의 공통 응답 클래스
 * 타임스탬프, 성공 여부, 메시지, 데이터 포함
 * author 이원재
 * since 2025.01.27
 */
@Getter
public class GenericResponse<T> {
    private final ZonedDateTime timestamp;  // 응답 발생 시간
    private final boolean isSuccess;        // 요청 성공 여부
    private final String message;           // 응답 메시지
    private final T data;                   // 응답 데이터

    // 빌더 패턴을 사용하여 다양한 방식으로 응답 객체를 생성할 수 있도록 함
    @Builder(access = AccessLevel.PRIVATE)
    private GenericResponse(T data, String message, boolean isSuccess) {
        this.timestamp = ZonedDateTime.now();
        this.data = data;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    /**
     * 요청 성공(반환 값 : 데이터 + 메시지)
     * @param data    응답 데이터
     * @param message 응답 메시지
     * return {@link GenericResponse} 생성된 응답 객체
     */
    public static <T> GenericResponse<T> of(T data, String message) {
        return GenericResponse.<T>builder()
                .data(data)
                .message(message)
                .isSuccess(true)
                .build();
    }

    /**
     * 요청 성공(반환 값 : 데이터)
     * @param data 응답 데이터
     * return {@link GenericResponse} 생성된 응답 객체
     */
    public static <T> GenericResponse<T> of(T data) {
        return GenericResponse.<T>builder()
                .data(data)
                .isSuccess(true)
                .build();
    }

    /**
     * 요청 성공(반환 값 : 메시지)
     * @param message 응답 메시지
     * return {@link GenericResponse} 생성된 응답 객체
     */
    public static <T> GenericResponse<T> of(String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .isSuccess(true)
                .build();
    }

    /**
     * 요청 성공(반환 값 없음)
     * return {@link GenericResponse} 생성된 응답 객체
     */
    public static <T> GenericResponse<T> of() {
        return GenericResponse.<T>builder()
                .isSuccess(true)
                .build();
    }
}
