package com.project.backend.domain.ranking.controller;

import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.dto.RankingDTO;
import com.project.backend.domain.ranking.service.RankingService;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<GenericResponse<List<RankingDTO>>> getWeeklyBookRanking() {
        List<RankingDTO> ranking = rankingService.getRanking(RankingType.WEEKLY_BOOKS);
        return ResponseEntity.ok(GenericResponse.of(ranking));
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
    public ResponseEntity<GenericResponse<List<RankingDTO>>> getWeeklyReviewRanking() {
        List<RankingDTO> ranking = rankingService.getRanking(RankingType.WEEKLY_REVIEWS);
        return ResponseEntity.ok(GenericResponse.of(ranking));
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
    public ResponseEntity<GenericResponse<List<RankingDTO>>> getDailyReviewsRanking() {
        List<RankingDTO> ranking = rankingService.getRanking(RankingType.DAILY_REVIEWS);
        return ResponseEntity.ok(GenericResponse.of(ranking));
    }
}
