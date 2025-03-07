package com.project.backend.domain.challenge.attendance.service;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import com.project.backend.domain.challenge.attendance.repository.AttendanceRepository;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.service.ReviewCommentService;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * 출석 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;

    public boolean checkTodayAttendance(long challengeId, long memberId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);

        Optional<Attendance> op = attendanceRepository.findByChallengeIdAndMemberIdAndCreatedAtBetween(challengeId, memberId, startOfDay, endOfDay);

        return op.isPresent();
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void validateAttendance(Challenge challenge, Member member) {

        if (!checkTodayAttendance(challenge.getId(), member.getId())) {
            Optional<Attendance> opAttendance = createAttendance(challenge, member);

            if (opAttendance.isEmpty()) {
                throw new ChallengeException(
                        ChallengeErrorCode.DAILY_VERIFICATION.getStatus(),
                        ChallengeErrorCode.DAILY_VERIFICATION.getErrorCode(),
                        ChallengeErrorCode.DAILY_VERIFICATION.getMessage()
                );
            }
            else {
                save(opAttendance.get());
            }
        }
        else {
            throw new ChallengeException(
                    ChallengeErrorCode.ALREADY_VALID.getStatus(),
                    ChallengeErrorCode.ALREADY_VALID.getErrorCode(),
                    ChallengeErrorCode.ALREADY_VALID.getMessage()
            );
        }
    }

    public Optional<Attendance> createAttendance(Challenge challenge, Member member) {

        return findTodayReview(member.getId())
                .map(review ->
                        Attendance.builder()
                                .challenge(challenge)
                                .member(member)
                                .checkType(Attendance.CheckType.REVIEW)
                                .writeId(review.getId())
                                .build()
                )
                .or(() -> findTodayComment(member.getId())
                        .map(comment ->
                                Attendance.builder()
                                        .challenge(challenge)
                                        .member(member)
                                        .checkType(Attendance.CheckType.COMMENT)
                                        .writeId(comment.getId())
                                        .build()
                        )
                );
    }

    private Optional<ReviewsDTO> findTodayReview(long memberId) {
        return Optional.ofNullable(reviewService.getUserReviews(memberId))
                .flatMap(reviews -> reviews.stream()
                        .filter(review -> review.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                        .findFirst());
    }

    private Optional<ReviewCommentDto> findTodayComment(long memberId) {
        return Optional.ofNullable(reviewCommentService.findUserComment(memberId))
                .flatMap(comments -> comments.stream()
                        .filter(comment -> comment.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                        .findFirst());
    }
}
