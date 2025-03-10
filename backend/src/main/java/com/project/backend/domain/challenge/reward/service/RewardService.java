package com.project.backend.domain.challenge.reward.service;

import com.project.backend.domain.challenge.attendance.service.AttendanceService;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final ChallengeService challengeService;
    private final EntryService entryService;
    private final AttendanceService attendanceService;

    @Transactional
    public void processRewards() {
        List<Challenge> challenges = challengeService.findChallengesInRefundProgress();

        for (Challenge challenge : challenges)
            refundInProgress(challenge.getId());
    }

    private void refundInProgress(Long challengeId) {
        // 1. 챌린지 정보 가져오기
        Challenge challenge = challengeService.getChallenge(challengeId);

        // 2. Entry 데이터 가져오기
        List<Entry> entries = entryService.findByChallengeId(challengeId);

        // 3. 참여율 계산 및 그룹 나누기
        Map<String, List<Entry>> groupedEntries = groupEntriesByParticipation(entries);

        // 4. 성공자, 부분 달성자, 실패자 처리
        List<Entry> successfulEntries = groupedEntries.get("SUCCESS");
        List<Entry> failedEntries = groupedEntries.get("FAILED");

        // 5. 실패자의 차감된 금액 계산, 분배
        long totalPenaltyAmount = calculatePenaltyAmount(failedEntries);

        // 6. 성공자의 추가 보상 분배
        distributeRewards(successfulEntries, totalPenaltyAmount, challenge.getTotalDeposit());

        challenge.setStatus(Challenge.ChallengeStatus.REFUNDING);
    }

    private Map<String, List<Entry>> groupEntriesByParticipation(List<Entry> entries) {
        return entries.stream().peek(entry -> {
                    double rate = entry.getRate();
                    if (rate >= 80) {
                        entry.setRefundAmount(entry.getDeposit());
                        entry.setRefunded(true);
                    }
                })
                .collect(Collectors.groupingBy(entry -> {
                    double rate = entry.getRate();

                    if (rate == 100) return "SUCCESS";
                    else if (rate >= 80) return "PARTIAL";
                    else return "FAILED";
                }));
    }

    private long calculatePenaltyAmount(List<Entry> failedEntries) {
        long totalPenalty = 0;

        for (Entry entry : failedEntries) {
            double participationRate = entry.getRate();
            double penaltyRate;

            if (participationRate >= 70) penaltyRate = 0.05;
            else if (participationRate >= 60) penaltyRate = 0.10;
            else penaltyRate = 0.15;

            long penaltyAmount = (long) (entry.getDeposit() * penaltyRate);
            totalPenalty += penaltyAmount;

            entry.setRefundAmount(entry.getDeposit() - penaltyAmount);
            entry.setRefunded(true);
        }

        return totalPenalty;
    }

    private void distributeRewards(List<Entry> successfulEntries, long totalPenaltyAmount, long totalDeposit) {
        long companyAmount = totalPenaltyAmount;
        for (Entry entry : successfulEntries) {
            double rewardRatio = entry.getDeposit() / (double) totalDeposit;
            long rewardAmount = (long) (rewardRatio * totalPenaltyAmount);

            entry.setRewardAmount(rewardAmount);
            entry.setRefundAmount(entry.getDeposit() + rewardAmount);
            entry.setRefunded(true);
            companyAmount -= rewardAmount;
        }

        //회사 돈
        System.out.println("companyAmount = " + companyAmount);
    }
}