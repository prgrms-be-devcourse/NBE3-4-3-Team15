package com.project.backend.domain.challenge.scheduler;

import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeScheduler {
    private final ChallengeService challengeService;

    @Scheduled(cron = "0 20 11 * * ?")
    public void updateChallengeStatus() {
        challengeService.updateChallengeStatus();
    }
}
