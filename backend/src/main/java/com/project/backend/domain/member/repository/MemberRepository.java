package com.project.backend.domain.member.repository;

import com.project.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * 회원 Repository
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
public interface MemberRepository extends JpaRepository<Member, String> {
}
