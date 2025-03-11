package com.project.backend.domain.ranking.common;

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
