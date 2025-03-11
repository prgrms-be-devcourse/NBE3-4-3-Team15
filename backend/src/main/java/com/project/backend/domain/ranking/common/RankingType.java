package com.project.backend.domain.ranking.common;

/**
 * 랭킹 유형을 정의하는 열거형 (Enum)
 * 각 랭킹 유형은 Redis에서 사용하는 고유한 키 값을 가짐.
 *
 * @author 김남우
 * @since 2025.03.11
 */
public enum RankingType {
    WEEKLY_BOOKS("weekly_books_ranking"),
    WEEKLY_REVIEWS("weekly_reviews_ranking"),
    DAILY_REVIEWS("daily_books_ranking");

    private final String key;

    RankingType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
