package com.project.backend.domain.follow.service;

import com.project.backend.domain.follow.dto.FollowResponseDto
import com.project.backend.domain.follow.entity.Follow
import com.project.backend.domain.follow.exception.FollowErrorCode
import com.project.backend.domain.follow.exception.FollowException
import com.project.backend.domain.follow.repository.FollowRepository
import com.project.backend.domain.member.entity.Member
import com.project.backend.domain.member.repository.MemberRepository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FollowService(
    private val followRepository: FollowRepository,
    private val memberRepository: MemberRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {

    // Redis 에 저장될 키 패턴 정의
    companion object {
        private const val FOLLOWING_PREFIX = "%s:following"      // 특정 사용자가 팔로우하는 대상 목록
        private const val FOLLOWERS_PREFIX = "%s:followers"      // 특정 사용자를 팔로우하는 사람 목록
        private const val PENDING_FOLLOWS = "pendingFollows"     // 팔로우 요청이 발생한 목록
        private const val PENDING_UNFOLLOWS = "pendingUnfollows" // 언팔로우 요청이 발생한 목록
    }

    /**
     * @param username 조회할 회원의 사용자명
     * @return 락이 걸린 상태의 Member 엔티티
     * @throws FollowException 회원을 찾을 수 없는 경우 발생
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private fun findMemberWithLock(username: String): Member {
        return memberRepository.findByUsername(username)
            .orElseThrow { FollowException(FollowErrorCode.NOT_FOUND_MEMBER) }
    }

    /**
     * 팔로우 기능
     *
     * @param followerId  팔로우를 요청하는 사용자 ID
     * @param followingId 팔로우 대상 사용자 ID
     * @return 팔로우 성공 메시지
     */
    fun follow(followerId: String, followingId: String): String {
        // 1. 비관적 락을 걸고 사용자 조회
        val follower = findMemberWithLock(followerId)
        val following = findMemberWithLock(followingId)

        // 자기 자신을 팔로우하려는 경우 예외 처리
        if (follower == following) {
            throw FollowException(FollowErrorCode.CAN_NOT_FOLLOW_MYSELF)
        }

        // Redis SetOperations 객체 생성
        val setOps = redisTemplate.opsForSet()

        // Redis 키 설정
        val followingKey = FOLLOWING_PREFIX.format(followerId)  // 팔로잉 목록 (followerId가 팔로우하는 대상)
        val followersKey = FOLLOWERS_PREFIX.format(followingId) // 팔로워 목록 (followingId를 팔로우하는 사람)
        val pendingFollowKey = "$followerId:$followingId" // 팔로우 요청을 식별하는 키 (ex: "1:2")
        val pendingUnfollowKey = "$followerId:$followingId" // 언팔로우 요청을 식별하는 키 (ex: "1:2")

        // 1. Redis 에 팔로우 정보가 이미 존재하는지 확인
        if (setOps.isMember(followingKey, followingId) == true) {
            // 1-1. Redis 에 팔로우 정보가 이미 존재하는 경우 예외 처리
            throw FollowException(FollowErrorCode.ALREADY_FOLLOWING)
        } else {
            // 1-2.Redis 에 팔로우 정보가 존재하지 않는 경우 -> DB에 팔로우 정보에 존재하는지 확인
            if (followRepository.findByFollowerAndFollowing(follower, following) != null) {
                // 1-2-1. DB에 팔로우 정보가 존재하는 경우 예외 처리
                throw FollowException(FollowErrorCode.ALREADY_FOLLOWING)
            } else {
                // 1-2-2. DB에 팔로우 정보가 존재하지 않는 경우 -> 팔로우 처리
                // pendingUnfollows 목록에 언팔로우 요청이 있는 경우 삭제
                if (setOps.isMember(PENDING_UNFOLLOWS, pendingUnfollowKey) == true) {
                    setOps.remove(PENDING_UNFOLLOWS, pendingUnfollowKey)
                }
                // 팔로우한 실제 데이터를 Redis 에 저장
                setOps.add(followingKey, followingId) // 팔로잉 목록에 추가
                setOps.add(followersKey, followerId) // 팔로워 목록에 추가

                // pendingFollows 목록에 팔로우 요청을 추가
                redisTemplate.opsForSet().add(PENDING_FOLLOWS, pendingFollowKey)
            }
        }
        return "팔로우 성공"
    }

    /**
     * 언팔로우 기능
     *
     * @param followerId  팔로우를 요청하는 사용자 ID
     * @param followingId 팔로우 대상 사용자 ID
     * @return 언팔로우 성공 메시지
     */
    fun unFollow(followerId: String, followingId: String): String {
        // 1. 비관적 락을 걸고 사용자 조회
        val follower = findMemberWithLock(followerId)
        val following = findMemberWithLock(followingId)

        // 자기 자신을 언팔로우하려는 경우 예외 처리
        if (follower == following) {
            throw FollowException(FollowErrorCode.CAN_NOT_UNFOLLOW_MYSELF)
        }

        // Redis SetOperations 객체 생성
        val setOps = redisTemplate.opsForSet()

        // Redis 키 설정
        val followingKey = FOLLOWING_PREFIX.format(followerId)  // 팔로잉 목록 (followerId가 팔로우한 대상)
        val followersKey = FOLLOWERS_PREFIX.format(followingId) // 팔로워 목록 (followingId를 팔로우한 사람)
        val pendingFollowKey = "$followerId:$followingId" // 팔로우 요청을 식별하는 키 (ex: "1:2")
        val pendingUnfollowKey = "$followerId:$followingId" // 언팔로우 요청을 식별하는 키 (ex: "1:2")

        // 1. Redis 에 팔로우 정보가 이미 존재하는지 확인
        if (setOps.isMember(followingKey, followingId) == true) {
            // 1-1. Redis 에 팔로우 정보가 존재하는 경우 -> 언팔로우 처리

            // pendingFollows 목록에 해당 팔로우 요청이 있는 경우 삭제
            if (setOps.isMember(PENDING_FOLLOWS, pendingFollowKey) == true) {
                setOps.remove(PENDING_FOLLOWS, pendingFollowKey)
            }

            // 팔로우 관계를 Redis 에서 삭제
            setOps.remove(followingKey, followingId) // 팔로잉 목록에서 제거
            setOps.remove(followersKey, followerId)  // 팔로워 목록에서 제거

            // pendingUnfollows 목록에 언팔로우 요청을 추가
            redisTemplate.opsForSet().add(PENDING_UNFOLLOWS, pendingUnfollowKey)
        } else {
            // 1-2. Redis 에 팔로우 정보가 존재하지 않는 경우 -> DB에 팔로우 정보가 존재하는지 확인
            if (followRepository.findByFollowerAndFollowing(follower, following) == null) {
                // 1-2-1. DB에 팔로우 정보가 존재하지 않는 경우 예외 처리
                throw FollowException(FollowErrorCode.NOT_FOLLOWING)
            } else {
                // 1-2-2. DB에 팔로우 정보가 존재하는 경우 pendingUnfollows 목록에 언팔로우 요청을 추가
                redisTemplate.opsForSet().add(PENDING_UNFOLLOWS, pendingUnfollowKey)
            }
        }
        return "언팔로우 성공"
    }

    /**
     * 사용자의 팔로잉 목록 조회
     * @param username 사용자 username
     * @return 팔로잉 정보 리스트
     */
    @Transactional(readOnly = true)
    fun getFollowings(username: String): List<FollowResponseDto> {
        val member = findMemberByUsername(username)
        val setOps = redisTemplate.opsForSet()

        // 팔로잉 목록에 대한 Redis 키 생성
        val followingKey = FOLLOWING_PREFIX.format(username)

        // Redis 에서 해당 사용자의 팔로잉 목록을 조회
        val followingIds = setOps.members(followingKey)

        // 1. Redis 에 팔로잉 목록이 이미 있으면, Redis 에서 해당 데이터를 바로 반환
        if (followingIds != null && followingIds.isNotEmpty()) {
            return followingIds.stream()
                .map { this.findMemberByUsername(it) } // username → Member 조회
                .map { following -> toFollowResponseDto(Follow(member, following), false) }
                .toList()
        }

        // 2. Redis 에 팔로잉 목록이 없으면, DB 에서 해당 데이터를 조회 후 Redis 에 저장
        val followings = followRepository.findByFollower(member)

        // DB 에서 팔로잉 목록을 조회한 후 Redis 에 저장
        if (followings.isNotEmpty()) {
            setOps.add(followingKey, *followings.stream()
                .map { f -> f.following.username }.distinct().toArray { size -> arrayOfNulls<String>(size) })
        }

        // DB 에서 조회된 팔로잉 목록을 FollowResponseDto 로 변환하여 반환
        return followings.stream()
            .map { f -> toFollowResponseDto(f, false) }
            .toList()
    }

    /**
     * 사용자의 팔로워 목록 조회
     * @param username 조회할 회원 username
     * @return 팔로워 정보 리스트
     */
    @Transactional(readOnly = true)
    fun getFollowers(username: String): List<FollowResponseDto> {
        val member = findMemberByUsername(username)
        val setOps = redisTemplate.opsForSet()

        // 팔로워 목록에 대한 Redis 키 생성
        val followersKey = FOLLOWERS_PREFIX.format(username)

        // Redis 에서 해당 사용자의 팔로워 목록을 조회
        val followerIds = setOps.members(followersKey)

        // 1. Redis 에 팔로워 목록이 이미 있으면, Redis 에서 해당 데이터를 바로 반환
        if (followerIds != null && followerIds.isNotEmpty()) {
            return followerIds.stream()
                .map { this.findMemberByUsername(it) } // username → Member 조회
                .map { follower -> toFollowResponseDto(Follow(follower, member), true) }
                .toList()
        }

        // 2. Redis 에 팔로워 목록이 없으면, DB 에서 해당 데이터를 조회 후 Redis 에 저장
        val followers = followRepository.findByFollowing(member)

        if (followers.isNotEmpty()) {
            setOps.add(followersKey, *followers.stream()
                .map { f -> f.follower.username }.distinct().toArray { size -> arrayOfNulls<String>(size) })
        }

        // DB 에서 조회된 팔로워 목록을 FollowResponseDto 로 변환하여 반환
        return followers.stream()
            .map { f -> toFollowResponseDto(f, true) }
            .toList()
    }

    /**
     * 회원 username 으로 회원 조회
     * @param username 회원 username
     * @return Member 엔티티
     */
    private fun findMemberByUsername(username: String): Member {
        return memberRepository.findByUsername(username)
            .orElseThrow { FollowException(FollowErrorCode.NOT_FOUND_MEMBER) }
    }

    /**
     * Follow 엔티티를 FollowResponseDto로 변환
     * @param follow 팔로우 엔티티
     * @param isFollowerList 팔로워 리스트인지 여부
     * @return FollowResponseDto
     */
    private fun toFollowResponseDto(follow: Follow, isFollowerList: Boolean): FollowResponseDto {
        val target = if (isFollowerList) follow.follower else follow.following

        // Redis 에서 followerCount, followingCount 가져오기
        val followersKey = FOLLOWERS_PREFIX.format(target.username)
        val followingKey = FOLLOWING_PREFIX.format(target.username)

        val followerCount = redisTemplate.opsForSet().size(followersKey)  // 팔로워 수
        val followingCount = redisTemplate.opsForSet().size(followingKey) // 팔로잉 수

        return FollowResponseDto(
            target.username,
            target.nickname,
            followerCount ?: 0,   // null 체크 후 반환
            followingCount ?: 0   // null 체크 후 반환
        )
    }
}
