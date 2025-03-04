package com.project.backend.domain.challenge.entry.repository;

import com.project.backend.domain.challenge.entry.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * 챌린지 참가 레포지토리
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
}
