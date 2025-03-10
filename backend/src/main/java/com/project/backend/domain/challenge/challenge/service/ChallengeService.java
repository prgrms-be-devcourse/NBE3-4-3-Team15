package com.project.backend.domain.challenge.challenge.service;

import com.project.backend.domain.challenge.attendance.service.AttendanceService;
import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.repository.ChallengeRepository;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeException(
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getStatus(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getErrorCode(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getMessage()
                ));
    }

    public Challenge join(long id, CustomUserDetails user, long deposit) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getUsername());

        entryService.join(challenge, member, deposit);
        challenge.addDeposit(deposit);

        return challenge;
    }

    public Challenge create(ChallengeDto challengeDto) {
        Challenge challenge = Challenge.builder()
                .name(challengeDto.getName())
                .content(challengeDto.getContent())
                .startDate(challengeDto.getStartDate())
                .endDate(challengeDto.getEndDate())
                .totalDeposit(0)
                .build();

        challenge.updateStatus();
        return challengeRepository.save(challenge);
    }


    public Challenge validation(long id, CustomUserDetails user) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getUsername());

        attendanceService.validateAttendance(challenge, member);

        return challenge;
    }

    public Optional<Challenge> findLatest() {
        return challengeRepository.findFirstByOrderByIdDesc();
    }

    @Transactional
    public void updateChallengeStatus() {
        challengeRepository.updateChallengeStatuses();

        entryService.updateIsActiveForEndedChallenges();
    }
}