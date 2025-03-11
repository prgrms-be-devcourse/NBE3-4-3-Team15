package com.project.backend.domain.ranking.scheduler;

import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * -- 랭킹 스케줄러 --
 *
 * @author -- 김남우 --
 * @since -- 2025.03.04 --
 */
@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingService rankingService;

    /**
     * 주간 랭킹을 갱신하는 메서드
     * 매시간 0분 0초마다 실행
     *
     * @author 김남우
     * @since 2025.03.09
     */
    @Scheduled(cron = "0 0 * * * *")
    public void updateWeeklyRanking(){
        updateRankingByPeriod(1, "주간");
    }

    /**
     * 일간 랭킹을 갱신하는 메서드
     * 매시간 10분마다 실행
     *
     * @author 김남우
     * @since 2025.03.10
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void updateDailyRanking(){
        updateRankingByPeriod(0, "일간");
    }

    /**
     * 특정 기간의 랭킹을 업데이트하는 내부 메서드
     *
     * @param weeksOrDays 랭킹을 계산할 기간 (1: 주간, 0: 일간)
     * @param periodType 랭킹 유형을 나타내는 문자열
     *
     * @author 김남우
     * @since 2025.03.11
     */
    private void updateRankingByPeriod(int weeksOrDays, String periodType) {
        LocalDateTime start = weeksOrDays > 0 ? LocalDateTime.now().minusWeeks(weeksOrDays) : LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        System.out.println(periodType + " 랭킹 업데이트 : " + end);

        if (weeksOrDays > 0) { // 주간 랭킹
            rankingService.updateRanking(RankingType.WEEKLY_BOOKS, start, end);
            rankingService.updateRanking(RankingType.WEEKLY_REVIEWS, start, end);
        } else { // 일간 랭킹
            rankingService.updateRanking(RankingType.DAILY_REVIEWS, start, end);
        }
    }
}
