package com.project.backend.domain.follow.repository;

import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Follow.FollowId> {

    /**
     * 특정 팔로워와 팔로잉 관계를 조회
     * @param follower 팔로우를 요청한 회원
     * @param following 팔로우 대상 회원
     * @return Follow 엔티티 (팔로우 관계가 없으면 null)
     */
    Follow findByFollowerAndFollowing(Member follower, Member following);

    /**
     * 특정 회원이 팔로우하는 사람 목록 조회
     * @param member 조회할 회원
     * @return 회원이 팔로우하는 사람 목록
     */
    @Query("SELECT f FROM Follow f WHERE f.follower = :member")
    List<Follow> findFollowingsByMember(Member member);

    /**
     * 특정 회원을 팔로우하는 사람 목록 조회
     * @param member 조회할 회원
     * @return 회원을 팔로우하는 사람 목록
     */
    @Query("SELECT f FROM Follow f WHERE f.following = :member")
    List<Follow> findFollowersByMember(Member member);

    /**
     * 특정 회원의 팔로워 수 조회
     * @param member 조회할 회원
     * @return 팔로워 수
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following = :member")
    long countFollowers(Member member);

    /**
     * 특정 회원이 팔로우하는 사람 수 조회
     * @param member 조회할 회원
     * @return 팔로잉 수
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower = :member")
    long countFollowings(Member member);
}
