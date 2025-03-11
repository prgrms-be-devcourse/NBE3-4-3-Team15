package com.project.backend.domain.follow.repository;

import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FollowRepository : JpaRepository<Follow, Follow.FollowId> {

    /**
     * 특정 팔로워와 팔로잉 관계를 조회
     * @param follower 팔로우를 요청한 회원
     * @param following 팔로우 대상 회원
     * @return Follow 엔티티 (팔로우 관계가 없으면 null)
     */
    fun findByFollowerAndFollowing(follower: Member, following: Member): Follow?

    /**
     * 특정 회원이 팔로우하는 사람 목록 조회
     * @param follower 조회할 회원
     * @return 회원이 팔로우하는 사람 목록
     */
    fun findByFollower(follower: Member): MutableList<Follow>

    /**
     * 특정 회원을 팔로잉하는 사람 목록 조회
     * @param following 조회할 회원
     * @return 회원을 팔로우하는 사람 목록
     */
    fun findByFollowing(following: Member): MutableList<Follow>
}
