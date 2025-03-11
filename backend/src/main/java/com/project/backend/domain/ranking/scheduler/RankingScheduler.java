package com.project.backend.domain.ranking.scheduler;

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
        LocalDateTime start = LocalDateTime.now().minusWeeks(1); // 1주일 전부터
        LocalDateTime end = LocalDateTime.now(); // 현재 시간까지

        System.out.println("주간 랭킹 업데이트 : " + end);

        rankingService.updateWeeklyBooksRanking(start, end);
        rankingService.updateWeeklyReviewsRanking(start, end);
    }

//    @Scheduled(cron = "0 */10 * * * *") // 매시간 10분 마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void updatedaliyRanking(){
        LocalDateTime start = LocalDateTime.now().minusDays(1); // 1일 전부터
        LocalDateTime end = LocalDateTime.now(); // 현재 시간까지

        System.out.println("일간 랭킹 업데이트 : " + end);

        rankingService.updateDailyReviewsRanking(start, end);
    }
}
