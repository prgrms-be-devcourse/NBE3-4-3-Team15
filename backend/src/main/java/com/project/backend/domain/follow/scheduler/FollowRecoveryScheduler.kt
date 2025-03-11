package com.project.backend.domain.follow.scheduler;

import com.project.backend.domain.follow.repository.FollowRepository
import com.project.backend.domain.member.repository.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FollowRecoveryScheduler(
    private val followRepository: FollowRepository,
    private val memberRepository: MemberRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisConnectionFactory: RedisConnectionFactory
) {
    private val log = LoggerFactory.getLogger(FollowRecoveryScheduler::class.java)

    // Redis 키 패턴 정의 (FollowService와 동일하게 유지)
    companion object {
        private const val FOLLOWING_PREFIX = "%s:following"
        private const val FOLLOWERS_PREFIX = "%s:followers"
    }

    /**
     * Redis 서버가 복구되었는지 주기적으로 확인하고, 복구 시 DB 데이터를 Redis에 동기화
     * 1초마다 실행되며, 연결이 끊어졌다가 복구되면 데이터를 동기화함
     */
    @Scheduled(fixedRate = 1000) // 1초마다 실행
    @Transactional(readOnly = true)
    fun checkRedisConnectionAndRecover() {
        try {
            // Redis 연결 확인
            val isConnected = redisConnectionFactory.connection.ping() != null

            if (isConnected) {
                // Redis 키 확인을 통해 데이터가 있는지 검사
                if (!hasRedisData()) {
                    log.info("[FollowRecoveryScheduler] Redis 서버가 복구되었고 데이터가 없어 DB 데이터를 동기화합니다.")
                    recoverRedisFromDatabase()
                }
            }
        } catch (e: Exception) {
            // 로그 빈도를 줄이기 위해 연결 오류는 debug 레벨로 로깅
            log.debug("[FollowRecoveryScheduler] Redis 연결 확인 중 오류 발생: {}", e.message)
        }
    }

    /**
     * Redis에 팔로우 관련 데이터가 있는지 확인
     * @return 데이터 존재 여부
     */
    private fun hasRedisData(): Boolean {
        try {
            // 샘플 사용자 (임의의 사용자)에 대한 데이터가 있는지 확인
            val sampleMembers = memberRepository.findAll()

            // 회원이 없는 경우 false 반환
            if (sampleMembers.isEmpty()) {
                return false
            }

            // 처음 10명의 회원만 확인 (성능 최적화)
            val checkLimit = minOf(10, sampleMembers.size)

            for (i in 0 until checkLimit) {
                val member = sampleMembers[i]
                val username = member.username

                // 해당 사용자의 팔로잉/팔로워 키가 있는지 확인
                val followingKey = FOLLOWING_PREFIX.format(username)
                val followersKey = FOLLOWERS_PREFIX.format(username)

                val followingKeyExists = redisTemplate.hasKey(followingKey)
                val followersKeyExists = redisTemplate.hasKey(followersKey)

                // 키가 존재하면 데이터가 있다고 판단
                if (followingKeyExists == true || followersKeyExists == true) {
                    return true
                }
            }

            // 확인한 모든 사용자에 대해 데이터가 없음
            return false
        } catch (e: Exception) {
            log.error("[FollowRecoveryScheduler] Redis 데이터 확인 중 오류 발생: {}", e.message)
            return false
        }
    }

    /**
     * DB의 팔로우 데이터를 Redis로 복구
     */
    private fun recoverRedisFromDatabase() {
        log.info("[FollowRecoveryScheduler] DB 데이터를 Redis로 복구 시작")
        val setOps = redisTemplate.opsForSet()

        try {
            // 모든 팔로우 관계를 DB에서 조회
            val allFollows = followRepository.findAll()

            for (follow in allFollows) {
                val follower = follow.follower
                val following = follow.following

                if (follower != null && following != null) {
                    val followerId = follower.username
                    val followingId = following.username

                    // Redis 키 설정
                    val followingKey = FOLLOWING_PREFIX.format(followerId)  // 팔로잉 목록
                    val followersKey = FOLLOWERS_PREFIX.format(followingId) // 팔로워 목록

                    // Redis에 팔로우 관계 데이터 추가
                    setOps.add(followingKey, followingId)
                    setOps.add(followersKey, followerId)
                }
            }

            log.info("[FollowRecoveryScheduler] 총 {}개의 팔로우 관계를 Redis에 복구 완료", allFollows.size)
        } catch (e: Exception) {
            log.error("[FollowRecoveryScheduler] Redis 데이터 복구 중 오류 발생: {}", e.message)
        }
    }
}