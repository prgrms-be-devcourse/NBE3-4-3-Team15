package com.project.backend.domain.challenge.entry.repository;

import com.project.backend.domain.challenge.entry.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 챌린지 참가 레포지토리
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByMemberId(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Entry e SET e.isActive = false WHERE e.challenge.id IN (SELECT c.id FROM Challenge c WHERE c.endDate <= CURRENT_DATE AND c.status = 'END')")
    void updateIsActiveForEndedChallenges();

    List<Entry> findByChallengeId(Long challengeId);
}
