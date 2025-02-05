package com.project.backend.domain.book.service;

/**
 * --도서에 쓰이는 유틸관련 클래스--
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
public class BookUtil {
    /**
     * -- ISBN 정제 메소드 --
     * 카카오 API에서 제공하는 ISBN은 "10자리ISBN 13자리ISBN" 형식이므로
     * 13자리 ISBN만 추출하여 반환한다.
     *
     * @param isbn 원본 ISBN 문자열
     * @return 정제된 13자리 ISBN
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    public static String extractIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return "";
        }
        String[] parts = isbn.split(" ");
        return (parts.length > 1) ? parts[1] : parts[0]; // 13자리 ISBN이 있으면 사용
    }
}
