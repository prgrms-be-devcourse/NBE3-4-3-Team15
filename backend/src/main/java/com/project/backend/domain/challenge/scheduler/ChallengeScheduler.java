package com.project.backend.domain.challenge.scheduler;

import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.challenge.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 챌린지 스케줄러
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {
    private final ChallengeService challengeService;
    private final RewardService rewardService;

    /**
     * 챌린지 상태 업데이트
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateChallengeStatus() {
        challengeService.updateChallengeStatus();
    }

    /**
     * 보상 처리
     */
    @Scheduled(cron = "0 20 1 * * ?")
    public void updateReward() {
        rewardService.processRewards();
    }
}
