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
 * @since -- 3월 4일 --
 */
@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingService rankingService;

//    @Scheduled(cron = "0 0 * * * *")  // 매시간 0분 0초에 실행
    @Scheduled(cron = "0 * * * * *")
    public void updateWeeklyRanking(){
        updateRankingByPeriod(1, "주간");
    }

    //    @Scheduled(cron = "0 */10 * * * *") // 매시간 10분 마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void updateDailyRanking(){
        updateRankingByPeriod(0, "일간");
    }

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
