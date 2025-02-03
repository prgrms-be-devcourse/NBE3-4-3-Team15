package com.project.backend.domain.member.repository;

import com.project.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * 회원 Repository
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
