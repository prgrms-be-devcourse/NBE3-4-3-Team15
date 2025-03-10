package com.project.backend.domain.challenge.reward.service;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import com.project.backend.domain.challenge.attendance.repository.AttendanceRepository;
import com.project.backend.domain.challenge.attendance.service.AttendanceService;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.repository.ChallengeRepository;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.repository.EntryRepository;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final ChallengeRepository challengeRepository;
    private final EntryRepository entryRepository;
    private final AttendanceRepository attendanceRepository;
    private final ChallengeService challengeService;
    private final EntryService entryService;
    private final AttendanceService attendanceService;
    private final MemberService memberService;

    @Transactional
    public void processRewards(Long challengeId) {
        // 1. 챌린지 정보 가져오기
        Challenge challenge = challengeService.getChallenge(challengeId);

        int totalDays = calculateTotalDays(challenge);

        // 2. 참가자의 출석 데이터 가져오기
        List<Attendance> attendances = attendanceService.findByChallengeId(challengeId);

        // 3. Entry 데이터 가져오기
        List<Entry> entries = entryService.findByChallengeId(challengeId);

        // 4. 참여율 계산 및 그룹 나누기
        Map<String, List<Entry>> groupedEntries = groupEntriesByParticipation(entries, attendances, totalDays);

        // 5. 성공자, 부분 달성자, 실패자 처리
        List<Entry> successfulEntries = groupedEntries.get("SUCCESS");
        List<Entry> partialEntries = groupedEntries.get("PARTIAL");
        List<Entry> failedEntries = groupedEntries.get("FAILED");

        // 6. 실패자의 차감된 금액 계산
        BigDecimal totalPenaltyAmount = calculatePenaltyAmount(failedEntries);

        // 7. 성공자의 추가 보상 분배
        distributeRewards(successfulEntries, totalPenaltyAmount, challenge.getTotalDeposit());

        // 8. 실패자 페널티 적용 및 환급
        applyPenaltiesAndRefunds(failedEntries);
    }

    private int calculateTotalDays(Challenge challenge) {
        return (int) (challenge.getEndDate().toLocalDate().toEpochDay() -
                challenge.getStartDate().toLocalDate().toEpochDay() + 1);
    }

    private Map<String, List<Entry>> groupEntriesByParticipation(List<Entry> entries, List<Attendance> attendances, int totalDays) {
        return entries.stream().collect(Collectors.groupingBy(entry -> {
            long memberAttendanceCount = attendances.stream()
                    .filter(attendance -> attendance.getMember().getId().equals(entry.getMember().getId()))
                    .count();

            double participationRate = (memberAttendanceCount / (double) totalDays) * 100;

            if (participationRate == 100) return "SUCCESS";
            else if (participationRate >= 80) return "PARTIAL";
            else return "FAILED";
        }));
    }

    private BigDecimal calculatePenaltyAmount(List<Entry> failedEntries) {
        BigDecimal totalPenalty = BigDecimal.ZERO;

        for (Entry entry : failedEntries) {
            double participationRate = entry.getParticipationRate(); // 이미 계산된 참여율 사용
            BigDecimal penaltyRate;

            if (participationRate >= 70) penaltyRate = BigDecimal.valueOf(0.05);
            else if (participationRate >= 60) penaltyRate = BigDecimal.valueOf(0.10);
            else penaltyRate = BigDecimal.valueOf(0.15);

            BigDecimal penaltyAmount = entry.getDeposit().multiply(penaltyRate);
            totalPenalty = totalPenalty.add(penaltyAmount);

            // 차감된 금액 저장
            entry.setRefundAmount(entry.getDeposit().subtract(penaltyAmount));
            entryRepository.save(entry);
        }

        return totalPenalty;
    }

    private void distributeRewards(List<Entry> successfulEntries, BigDecimal totalPenaltyAmount, long totalDeposit) {
        for (Entry entry : successfulEntries) {
            BigDecimal rewardRatio = entry.getDeposit().divide(totalDeposit, 10, BigDecimal.ROUND_DOWN);
            BigDecimal rewardAmount = rewardRatio.multiply(totalPenaltyAmount);

            // 추가 보상 저장
            entry.setRewardAmount(rewardAmount);
            entry.setRefundAmount(entry.getDeposit().add(rewardAmount));
            entryRepository.save(entry);
        }
    }

    private void applyPenaltiesAndRefunds(List<Entry> failedEntries) {
        for (Entry entry : failedEntries) {
            entry.setRefunded(true); // 환급 완료 표시
            entryRepository.save(entry);
        }
    }
}