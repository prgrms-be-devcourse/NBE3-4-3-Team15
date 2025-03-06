package com.project.backend.domain.challenge.challenge.repository;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
