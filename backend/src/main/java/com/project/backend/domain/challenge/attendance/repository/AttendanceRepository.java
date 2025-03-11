package com.project.backend.domain.challenge.attendance.repository;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 챌린지 레포지토리
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByChallengeIdAndMemberIdAndCreatedAtBetween(long challengeId, long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findByMemberIdAndCreatedAtBetween(Long id, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Attendance> findByChallengeIdAndMemberId(long challengeId, long memberId);

    List<Attendance> findByChallengeId(Long challengeId);

    long countByChallengeIdAndMemberId(Long id, Long id1);
}
