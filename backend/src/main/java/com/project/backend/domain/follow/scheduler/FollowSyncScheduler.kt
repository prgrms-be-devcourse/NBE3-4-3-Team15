package com.project.backend.domain.follow.scheduler;

import com.project.backend.domain.follow.entity.Follow
import com.project.backend.domain.follow.repository.FollowRepository
import com.project.backend.domain.member.repository.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FollowSyncScheduler(
    private val followRepository: FollowRepository,
    private val memberRepository: MemberRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(FollowSyncScheduler::class.java)

    companion object {
        private const val PENDING_FOLLOWS = "pendingFollows"
        private const val PENDING_UNFOLLOWS = "pendingUnfollows"
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    fun syncFollowData() {
        log.info("[FollowSyncScheduler] Redis와 DB 동기화 시작")
        val setOps = redisTemplate.opsForSet()

        // 1. pendingFollows 처리 (새로운 팔로우 관계 추가)
        val followRequests = setOps.members(PENDING_FOLLOWS)
        if (followRequests != null) {
            for (request in followRequests) {
                val ids = request.split(":")
                val followerId = ids[0]
                val followingId = ids[1]

                val follower = memberRepository.findByUsername(followerId).orElse(null)
                val following = memberRepository.findByUsername(followingId).orElse(null)

                if (follower != null && following != null) {
                    if (followRepository.findByFollowerAndFollowing(follower, following) == null) {
                        followRepository.save(Follow(follower, following))
                    }
                }
                setOps.remove(PENDING_FOLLOWS, request) // 처리된 요청 제거
            }
        }

        // 2. pendingUnfollows 처리 (언팔로우 관계 삭제)
        val unfollowRequests = setOps.members(PENDING_UNFOLLOWS)
        if (unfollowRequests != null) {
            for (request in unfollowRequests) {
                val ids = request.split(":")
                val followerId = ids[0]
                val followingId = ids[1]

                val follower = memberRepository.findByUsername(followerId).orElse(null)
                val following = memberRepository.findByUsername(followingId).orElse(null)

                if (follower != null && following != null) {
                    val follow = followRepository.findByFollowerAndFollowing(follower, following)
                    if (follow != null) {
                        followRepository.delete(follow)
                    }
                }
                setOps.remove(PENDING_UNFOLLOWS, request) // 처리된 요청 제거
            }
        }

        log.info("[FollowSyncScheduler] Redis와 DB 동기화 완료")
    }
}