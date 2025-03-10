package com.project.backend.domain.challenge.challenge.repository;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 *
 * 챌린지 레포지토리
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findFirstByOrderByIdDesc();

    @Modifying
    @Transactional
    @Query("UPDATE Challenge c " +
            "SET c.status = CASE " +
            "WHEN c.status = 'WAITING' AND c.startDate <= CURRENT_TIMESTAMP THEN 'START' " +
            "WHEN c.status = 'START' AND c.endDate <= CURRENT_TIMESTAMP THEN 'REFUNDING' " +
            "END " +
            "WHERE (c.status = 'WAITING' AND c.startDate <= CURRENT_TIMESTAMP) " +
            "OR (c.status = 'START' AND c.endDate <= CURRENT_TIMESTAMP)")
    void updateChallengeStatuses();

    @Query("SELECT c FROM Challenge c WHERE c.status = 'REFUNDING'")
    List<Challenge> findChallengesInRefundProgress();

    List<Challenge> findByStatus(Challenge.ChallengeStatus status);
}
