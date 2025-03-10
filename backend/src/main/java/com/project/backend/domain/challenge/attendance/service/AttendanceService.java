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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        List<ReviewsDTO> todayReviews = findTodayReviews(member.getId());
        List<ReviewCommentDto> todayComments = findTodayComments(member.getId());

        List<Attendance> todayAttendances = attendanceRepository.findByMemberIdAndCreatedAtBetween(
                member.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1)
        );

        Optional<ReviewsDTO> validReview = todayReviews.stream()
                .filter(review -> todayAttendances.stream()
                        .noneMatch(attendance ->
                                attendance.getCheckType() == Attendance.CheckType.REVIEW &&
                                        attendance.getWriteId() == review.getId()))
                .findFirst();

        if (validReview.isPresent()) {
            return Optional.of(
                    Attendance.builder()
                            .challenge(challenge)
                            .member(member)
                            .checkType(Attendance.CheckType.REVIEW)
                            .writeId(validReview.get().getId())
                            .build()
            );
        }

        // 오늘 작성된 댓글 중 유효한 데이터 선택
        Optional<ReviewCommentDto> validComment = todayComments.stream()
                .filter(comment -> todayAttendances.stream()
                        .noneMatch(attendance ->
                                attendance.getCheckType() == Attendance.CheckType.COMMENT &&
                                        attendance.getWriteId() == comment.getId()))
                .findFirst();

        if (validComment.isPresent()) {
            return Optional.of(
                    Attendance.builder()
                            .challenge(challenge)
                            .member(member)
                            .checkType(Attendance.CheckType.COMMENT)
                            .writeId(validComment.get().getId())
                            .build()
            );
        }

        // 유효한 리뷰나 댓글이 없으면 빈 값 반환
        return Optional.empty();
    }

    private List<ReviewsDTO> findTodayReviews(long memberId) {
        return Optional.ofNullable(reviewService.getUserReviews(memberId))
                .orElse(Collections.emptyList())
                .stream()
                .filter(review -> review.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
    }

    private List<ReviewCommentDto> findTodayComments(long memberId) {
        return Optional.ofNullable(reviewCommentService.findUserComment(memberId))
                .orElse(Collections.emptyList())
                .stream()
                .filter(comment -> comment.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
