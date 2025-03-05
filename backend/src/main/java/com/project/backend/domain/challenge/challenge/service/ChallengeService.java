package com.project.backend.domain.challenge.challenge.service;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import com.project.backend.domain.challenge.attendance.service.AttendanceService;
import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.repository.ChallengeRepository;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void join(long id, CustomUserDetails user, long deposit) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getUsername());

        entryService.join(challenge, member, deposit);
        challenge.addDeposit(deposit);
    }

    public Challenge create(ChallengeDto challengeDto) {
        LocalDateTime now = LocalDateTime.now();
        Challenge.ChallengeStatus status;

        if (now.isBefore(challengeDto.getStartDate())) {
            status = Challenge.ChallengeStatus.WAITING;
        } else if (now.isAfter(challengeDto.getEndDate())) {
            status = Challenge.ChallengeStatus.END;
        } else {
            status = Challenge.ChallengeStatus.START;
        }

        Challenge challenge = Challenge.builder()
                .name(challengeDto.getName())
                .content(challengeDto.getContent())
                .startDate(challengeDto.getStartDate())
                .endDate(challengeDto.getEndDate())
                .status(status)
                .totalDeposit(0)
                .build();

        return challengeRepository.save(challenge);
    }

    public Challenge validation(long id, CustomUserDetails user) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getUsername());

        if (attendanceService.getCheck(challenge.getId(), member.getId())) {
            Optional<Review> opReview = Optional.ofNullable(member.getRecommendReviews())
                    .flatMap(
                            reviews -> reviews.stream()
                                    .filter(review -> review.getCreatedAt().toLocalDate().isEqual(LocalDate.now()))
                                    .findFirst()
                    );

            if (opReview.isPresent()) {
                Attendance attendance = Attendance.builder()
                        .challenge(challenge)
                        .member(member)
                        .checkType(Attendance.CheckType.REVIEW)
                        .writeId(opReview.get().getId())
                        .build();

                attendanceService.save(attendance);
            }
        }

        return challenge;
    }
}
