package com.project.backend.domain.ranking.controller;

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
 * @since -- 3월 4일 --
 */
@Tag(name = "Ranking", description = "랭킹 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
@SecurityRequirement(name = "bearerAuth")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/weekly/book")
    @Operation(summary = "주간 인기 도서 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyBookRanking() {
        List<Map<String, Object>> weeklyBookRanking = rankingService.getWeeklyBookRanking();
        return ResponseEntity.ok(weeklyBookRanking);
    }

    @GetMapping("/weekly/review")
    @Operation(summary = "주간 인기 리뷰 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyReviewRanking() {
        List<Map<String, Object>> weeklyReviewRanking = rankingService.getWeeklyReviewRanking();
        return ResponseEntity.ok(weeklyReviewRanking);
    }

    @GetMapping("/daliy/review")
    @Operation(summary = "인기 급상승 리뷰 랭킹")
    public ResponseEntity<List<Map<String, Object>>> getDailyReviewsRanking() {
        List<Map<String, Object>> daliyReviewRanking = rankingService.getDailyReviewsRanking();
        return ResponseEntity.ok(daliyReviewRanking);
    }
}
