package com.project.backend.domain.challenge.challenge.service;

import com.project.backend.domain.challenge.attendance.service.AttendanceService;
import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.repository.ChallengeRepository;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 챌린지 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberService memberService;
    private final EntryService entryService;
    private final AttendanceService attendanceService;

    /**
     * ID로 챌린지 조회
     *
     * @param id 챌린지 ID
     * @return 챌린지 정보
     */
    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeException(
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getStatus(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getErrorCode(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getMessage()
                ));
    }

    /**
     * 챌린지 참가
     *
     * @param challengeId 챌린지 ID
     * @param member      회원 정보
     * @param deposit     예치금
     * @return 참가한 챌린지 정보
     */
    public Challenge join(long challengeId, Member member, long deposit) {
        Challenge challenge = getChallenge(challengeId);

        entryService.join(challenge, member, deposit);
        challenge.plusDeposit(deposit);
        member.plusDeposit(deposit);

        return challenge;
    }

    /**
     * 챌린지 참가 취소
     *
     * @param challengeId 챌린지 ID
     * @param member      회원 정보
     * @return 참가 취소한 챌린지 정보
     */
    public Challenge quit(long challengeId, Member member) {
        Challenge challenge = getChallenge(challengeId);

        if (challenge.getStatus().equals(Challenge.ChallengeStatus.WAITING)) {
            Entry entry = entryService.findByChallengeIdAndMemberId(challenge.getId(), member.getId());

            challenge.minusDeposit(entry.getDeposit());
            member.minusDeposit(entry.getDeposit());
            entryService.quit(entry);
        } else {
            throw new ChallengeException(
                    ChallengeErrorCode.CANCEL_IMPOSSIBLE.getStatus(),
                    ChallengeErrorCode.CANCEL_IMPOSSIBLE.getErrorCode(),
                    ChallengeErrorCode.CANCEL_IMPOSSIBLE.getMessage()
            );
        }

        return challenge;
    }

    /**
     * 챌린지 생성
     *
     * @param challengeDto 챌린지 정보
     * @return 생성된 챌린지 정보
     */
    public Challenge create(ChallengeDto challengeDto) {
        Challenge challenge = Challenge.builder()
                .name(challengeDto.getName())
                .content(challengeDto.getContent())
                .startDate(challengeDto.getStartDate().atStartOfDay())
                .endDate(challengeDto.getEndDate().atTime(LocalTime.MAX))
                .totalDeposit(0)
                .build();

        challenge.updateStatus();
        return challengeRepository.save(challenge);
    }

    /**
     * 챌린지 인증
     *
     * @param id   챌린지 ID
     * @param user 인증된 사용자 정보
     * @return 인증된 챌린지 정보
     */
    public Challenge validation(long id, CustomUserDetails user) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getUsername());

        attendanceService.validateAttendance(challenge, member);

        return challenge;
    }

    /**
     * 최신 챌린지 조회 (Optional)
     *
     * @return 최신 챌린지 정보
     */
    public Optional<Challenge> findLatest() {
        return challengeRepository.findFirstByOrderByIdDesc();
    }

    /**
     * 모든 챌린지 상태 업데이트
     */
    @Transactional
    public void updateChallengeStatus() {
        challengeRepository.updateChallengeStatuses();

        entryService.updateIsActiveForEndedChallenges();
    }

    /**
     * 환불 진행 중인 챌린지 목록 조회
     *
     * @return 환불 진행 중인 챌린지 목록
     */
    public List<Challenge> findChallengesInRefundProgress() {
        return challengeRepository.findChallengesInRefundProgress();
    }

    /**
     * 상태별 챌린지 목록 조회
     *
     * @param status 챌린지 상태
     * @return 상태별 챌린지 목록
     */
    public List<ChallengeDto> findByStatus(Challenge.ChallengeStatus status) {
        List<Challenge> challenges = challengeRepository.findByStatus(status);

        if (challenges.isEmpty()) {
            throw new ChallengeException(
                    ChallengeErrorCode.CHALLENGE_NOT_FOUND.getStatus(),
                    ChallengeErrorCode.CHALLENGE_NOT_FOUND.getErrorCode(),
                    ChallengeErrorCode.CHALLENGE_NOT_FOUND.getMessage()
            );
        }

        return challenges.stream()
                .map(ChallengeDto::new)
                .toList();
    }
}