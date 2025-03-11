package com.project.backend.domain.ranking.controller;

import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * -- 랭킹 컨트롤러 --
 *
 * @author -- 김남우 --
 * @since -- 2025.03.04 --
 */
@Tag(name = "Ranking", description = "랭킹 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
@SecurityRequirement(name = "bearerAuth")
public class RankingController {

    private final RankingService rankingService;

    /**
     * 주간 인기 도서 랭킹 조회 API
     *
     * @return 주간 인기 도서 목록
     *
     * @author 김남우
     * @since 2025.03.05
     */
    @GetMapping("/weekly/book")
    @Operation(summary = "주간 인기 도서 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyBookRanking() {
        return getRankingResponse(RankingType.WEEKLY_BOOKS);
    }

    /**
     * 주간 인기 리뷰 랭킹 조회 API
     *
     * @return 주간 인기 리뷰 목록
     *
     * @author 김남우
     * @since 2025.03.09
     */
    @GetMapping("/weekly/review")
    @Operation(summary = "주간 인기 리뷰 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyReviewRanking() {
        return getRankingResponse(RankingType.WEEKLY_REVIEWS);
    }

    /**
     * 일간 인기 리뷰 랭킹 조회 API
     *
     * @return 일간 인기 리뷰 목록
     *
     * @author 김남우
     * @since 2025.03.10
     */
    @GetMapping("/daily/review")
    @Operation(summary = "인기 급상승 리뷰 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getDailyReviewsRanking() {
        return getRankingResponse(RankingType.DAILY_REVIEWS);
    }

    /**
     * 지정된 랭킹 유형에 따라 랭킹 데이터를 조회하는 내부 메서드
     *
     * @param rankingType 조회할 랭킹 유형
     * @return 랭킹 리스트
     *
     * @author 김남우
     * @since 2025.03.11
     */
    private ResponseEntity<List<Map<String, Object>>> getRankingResponse(RankingType rankingType) {
        return ResponseEntity.ok(rankingService.getRanking(rankingType));
    }
}
