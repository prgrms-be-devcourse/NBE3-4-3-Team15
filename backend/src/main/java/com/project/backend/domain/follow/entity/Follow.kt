package com.project.backend.domain.follow.entity;

import com.project.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 팔로우 엔티티
 * 회원 간 팔로우 관계를 나타내는 엔티티
 * 각 팔로우 관계는 팔로워(follower)와 팔로잉(following)으로 구성
 * 팔로워는 팔로잉을 팔로우하는 회원을 의미 | 팔로잉은 팔로워를 팔로우하는 회원을 의미
 * 복합 기본 키를 사용하여 회원 간 팔로우 관계를 식별
 * 팔로워와 팔로잉은 회원 엔티티와 다대다(N:M) 관계
 * author: 이원재
 * since: 2025-01-27
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 제한
@Entity
@EntityListeners(AuditingEntityListener.class)
@IdClass(Follow.FollowId.class) // 복합 키를 정의하기 위해 IdClass 사용
public class Follow {

    /**
     * 팔로워
     * ManyToOne 관계로 연결 / LAZY 로딩
     * follower_id 컬럼과 연결
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    /**
     * 팔로잉
     * ManyToOne 관계로 연결 / LAZY 로딩
     * following_id 컬럼과 연결
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private Member following;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 팔로우 생성자
     * @param follower 팔로워(팔로우 요청 회원)
     * @param following 팔로잉(팔로우 대상 회원)
     */
    public Follow(Member follower, Member following) {
        this.follower = follower;
        this.following = following;
    }

    /**
     * FollowId 클래스
     * 팔로우 엔티티의 복합 기본 키 클래스
     * Serializable 인터페이스를 구현하여 직렬화 가능
     */
    @Getter
    @NoArgsConstructor
    public static class FollowId implements Serializable {
        private Long follower;  // 팔로워 ID
        private Long following; // 팔로잉 ID

        /**
         * FollowId 생성자
         * @param follower 팔로워 ID
         * @param following 팔로잉 ID
         */
        public FollowId(Long follower, Long following) {
            this.follower = follower;
            this.following = following;
        }
    }
}
