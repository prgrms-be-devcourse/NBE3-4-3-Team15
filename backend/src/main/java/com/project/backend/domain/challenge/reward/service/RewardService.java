package com.project.backend.domain.challenge.reward.service;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 보상 서비스
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Service
@RequiredArgsConstructor
public class RewardService {

    private final ChallengeService challengeService;
    private final EntryService entryService;
    private final MemberService memberService;

    /**
     * 보상 처리 (환불 진행 중인 챌린지에 대해)
     */
    @Transactional
    public void processRewards() {
        List<Challenge> challenges = challengeService.findChallengesInRefundProgress();

        for (Challenge challenge : challenges)
            refundInProgress(challenge.getId());
    }

    /**
     * 환불 및 보상 처리 (챌린지 ID 기준)
     *
     * @param challengeId 챌린지 ID
     */
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

    /**
     * 참가 기록을 참여율에 따라 성공, 부분 성공, 실패로 그룹화
     *
     * @param entries 참가 기록 목록
     * @return 그룹화된 참가 기록
     */
    private Map<String, List<Entry>> groupEntriesByParticipation(List<Entry> entries) {
        return entries.stream().peek(entry -> {
                    double rate = entry.getRate();
                    if (rate >= 80) {
                        entry.setRefundAmount(entry.getDeposit());
                        entry.setRefunded(true);
                        entry.getMember().minusDeposit(entry.getDeposit());
                    }
                })
                .collect(Collectors.groupingBy(entry -> {
                    double rate = entry.getRate();

                    if (rate == 100) return "SUCCESS";
                    else if (rate >= 80) return "PARTIAL";
                    else return "FAILED";
                }));
    }

    /**
     * 실패한 참가 기록의 패널티 금액 계산
     *
     * @param failedEntries 실패한 참가 기록 목록
     * @return 총 패널티 금액
     */
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
            entry.getMember().minusDeposit(entry.getDeposit());
        }

        return totalPenalty;
    }

    /**
     * 성공한 참가 기록에 대한 보상 분배
     *
     * @param successfulEntries  성공한 참가 기록 목록
     * @param totalPenaltyAmount 총 패널티 금액
     * @param totalDeposit       챌린지 총 예치금
     */
    private void distributeRewards(List<Entry> successfulEntries, long totalPenaltyAmount, long totalDeposit) {
        long companyAmount = totalPenaltyAmount;
        for (Entry entry : successfulEntries) {
            double rewardRatio = entry.getDeposit() / (double) totalDeposit;
            long rewardAmount = (long) (rewardRatio * totalPenaltyAmount);

            entry.setRewardAmount(rewardAmount);
            entry.setRefundAmount(entry.getDeposit() + rewardAmount);
            entry.setRefunded(true);
            entry.getMember().minusDeposit(entry.getDeposit());

            companyAmount -= rewardAmount;
        }

        Member member = memberService.getMemberByUsername("admin");
        member.setDeposit(companyAmount);
    }
}