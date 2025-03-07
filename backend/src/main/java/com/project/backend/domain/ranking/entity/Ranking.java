package com.project.backend.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * -- 랭킹 엔티티 --
 *
 * @author -- 김남우 --
 * @since -- 3월 4일 --
 */
@Entity
@Getter
@Setter
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // 랭킹 유형 (주간/인기, 주간/추천, 일간/급상승)

    @Column(nullable = false)
    private Long item; // 랭킹 항목 (책 ID 또는 리뷰 ID)

    @Column(nullable = false)
    private Double score; // 랭킹 점수

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 랭킹 업데이트 시간
}