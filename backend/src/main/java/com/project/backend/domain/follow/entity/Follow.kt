package com.project.backend.domain.follow.entity

import com.project.backend.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime

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
@Entity
@EntityListeners(AuditingEntityListener::class)
@IdClass(Follow.FollowId::class) // 복합 키를 정의하기 위해 IdClass 사용
class Follow {

    /**
     * 팔로워
     * ManyToOne 관계로 연결 / LAZY 로딩
     * follower_id 컬럼과 연결
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    lateinit var follower: Member

    /**
     * 팔로잉
     * ManyToOne 관계로 연결 / LAZY 로딩
     * following_id 컬럼과 연결
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    lateinit var following: Member

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    /**
     * JPA를 위한 protected 기본 생성자
     */
    protected constructor()

    /**
     * 팔로우 생성자
     * @param follower 팔로워(팔로우 요청 회원)
     * @param following 팔로잉(팔로우 대상 회원)
     */
    constructor(follower: Member, following: Member) {
        this.follower = follower
        this.following = following
    }

    /**
     * FollowId 클래스
     * 팔로우 엔티티의 복합 기본 키 클래스
     * Serializable 인터페이스를 구현하여 직렬화 가능
     */
    class FollowId : Serializable {
        var follower: Long? = null  // 팔로워 ID
        var following: Long? = null // 팔로잉 ID

        constructor() // 기본 생성자

        /**
         * FollowId 생성자
         * @param follower 팔로워 ID
         * @param following 팔로잉 ID
         */
        constructor(follower: Long, following: Long) {
            this.follower = follower
            this.following = following
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FollowId) return false

            if (follower != other.follower) return false
            if (following != other.following) return false

            return true
        }

        override fun hashCode(): Int {
            var result = follower?.hashCode() ?: 0
            result = 31 * result + (following?.hashCode() ?: 0)
            return result
        }
    }
}