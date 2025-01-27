package com.project.backend.global.response;

/**
 * ErrorDetail
 * 에러 발생 시 클라이언트에게 전달할 에러 상세 정보를 담는 클래스
 * @param field 에러가 발생한 필드
 * @param reason 에러 발생 원인
 * author 이원재
 * since 2021.01.27
 */
public record ErrorDetail(String field, String reason) {
    /**
     * ErrorDetail 생성 팩토리 메서드
     * @param field
     * @param reason
     * @return {@link ErrorDetail}
     */
    public static ErrorDetail of(String field, String reason) {
        return new ErrorDetail(field, reason);
    }
}
