package com.project.backend.domain.follow.service;

import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.exception.FollowErrorCode;
import com.project.backend.domain.follow.exception.FollowException;
import com.project.backend.domain.follow.repository.FollowRepository;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // Redis 에 저장될 키 패턴 정의
    private static final String FOLLOWING_PREFIX = "%s:following";      // 특정 사용자가 팔로우하는 대상 목록
    private static final String FOLLOWERS_PREFIX = "%s:followers";      // 특정 사용자를 팔로우하는 사람 목록
    private static final String PENDING_FOLLOWS = "pendingFollows";     // 팔로우 요청이 발생한 목록
    private static final String PENDING_UNFOLLOWS = "pendingUnfollows"; // 언팔로우 요청이 발생한 목록

    /**
     * 팔로우 기능
     *
     * @param followerId  팔로우를 요청하는 사용자 ID
     * @param followingId 팔로우 대상 사용자 ID
     * @return 팔로우 성공 메시지
     */
    public String follow(String followerId, String followingId) {
        // 팔로우를 요청한 사용자와 대상 사용자를 조회
        Member follower = findMemberByUsername(followerId);
        Member following = findMemberByUsername(followingId);

        // 자기 자신을 팔로우하려는 경우 예외 처리
        if (follower.equals(following)) {
            throw new FollowException(FollowErrorCode.CAN_NOT_FOLLOW_MYSELF);
        }

        // Redis SetOperations 객체 생성
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        // Redis 키 설정
        String followingKey = String.format(FOLLOWING_PREFIX, followerId);  // 팔로잉 목록 (followerId가 팔로우하는 대상)
        String followersKey = String.format(FOLLOWERS_PREFIX, followingId); // 팔로워 목록 (followingId를 팔로우하는 사람)
        String pendingFollowKey = followerId + ":" + followingId; // 팔로우 요청을 식별하는 키 (ex: "1:2")
        String pendingUnfollowKey = followerId + ":" + followingId; // 언팔로우 요청을 식별하는 키 (ex: "1:2")

        // 1. Redis 에 팔로우 정보가 이미 존재하는지 확인
        if (Boolean.TRUE.equals(setOps.isMember(followingKey, followingId))) {
            // 1-1. Redis 에 팔로우 정보가 이미 존재하는 경우 예외 처리
            throw new FollowException(FollowErrorCode.ALREADY_FOLLOWING);
        } else {
            // 1-2.Redis 에 팔로우 정보가 존재하지 않는 경우 -> DB에 팔로우 정보에 존재하는지 확인
            if (followRepository.findByFollowerAndFollowing(follower, following) != null) {
                // 1-2-1. DB에 팔로우 정보가 존재하는 경우 예외 처리
                throw new FollowException(FollowErrorCode.ALREADY_FOLLOWING);
            } else {
                // 1-2-2. DB에 팔로우 정보가 존재하지 않는 경우 -> 팔로우 처리
                // pendingUnfollows 목록에 언팔로우 요청이 있는 경우 삭제
                if (Boolean.TRUE.equals(setOps.isMember(PENDING_UNFOLLOWS, pendingUnfollowKey))) {
                    setOps.remove(PENDING_UNFOLLOWS, pendingUnfollowKey);
                }
                // 팔로우한 실제 데이터를 Redis 에 저장
                setOps.add(followingKey, followingId); // 팔로잉 목록에 추가
                setOps.add(followersKey, followerId); // 팔로워 목록에 추가

                // pendingFollows 목록에 팔로우 요청을 추가
                redisTemplate.opsForSet().add(PENDING_FOLLOWS, pendingFollowKey);
            }
        }
        return "팔로우 성공";
    }

    /**
     * 언팔로우 기능
     *
     * @param followerId  팔로우를 요청하는 사용자 ID
     * @param followingId 팔로우 대상 사용자 ID
     * @return 언팔로우 성공 메시지
     */
    public String unFollow(String followerId, String followingId) {
        // 언팔로우를 요청한 사용자와 대상 사용자를 조회
        Member follower = findMemberByUsername(followerId);
        Member following = findMemberByUsername(followingId);

        // 자기 자신을 언팔로우하려는 경우 예외 처리
        if (follower.equals(following)) {
            throw new FollowException(FollowErrorCode.CAN_NOT_UNFOLLOW_MYSELF);
        }

        // Redis SetOperations 객체 생성
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        // Redis 키 설정
        String followingKey = String.format(FOLLOWING_PREFIX, followerId);  // 팔로잉 목록 (followerId가 팔로우한 대상)
        String followersKey = String.format(FOLLOWERS_PREFIX, followingId); // 팔로워 목록 (followingId를 팔로우한 사람)
        String pendingFollowKey = followerId + ":" + followingId; // 팔로우 요청을 식별하는 키 (ex: "1:2")
        String pendingUnfollowKey = followerId + ":" + followingId; // 언팔로우 요청을 식별하는 키 (ex: "1:2")

        // 1. Redis 에 팔로우 정보가 이미 존재하는지 확인
        if (Boolean.TRUE.equals(setOps.isMember(followingKey, followingId))) {
            // 1-1. Redis 에 팔로우 정보가 존재하는 경우 -> 언팔로우 처리

            // pendingFollows 목록에 해당 팔로우 요청이 있는 경우 삭제
            if (Boolean.TRUE.equals(setOps.isMember(PENDING_FOLLOWS, pendingFollowKey))) {
                setOps.remove(PENDING_FOLLOWS, pendingFollowKey);
            }

            // 팔로우 관계를 Redis 에서 삭제
            setOps.remove(followingKey, followingId); // 팔로잉 목록에서 제거
            setOps.remove(followersKey, followerId);  // 팔로워 목록에서 제거

            // pendingUnfollows 목록에 언팔로우 요청을 추가
            redisTemplate.opsForSet().add(PENDING_UNFOLLOWS, pendingUnfollowKey);
        } else {
            // 1-2. Redis 에 팔로우 정보가 존재하지 않는 경우 -> DB에 팔로우 정보가 존재하는지 확인
            if (followRepository.findByFollowerAndFollowing(follower, following) == null) {
                // 1-2-1. DB에 팔로우 정보가 존재하지 않는 경우 예외 처리
                throw new FollowException(FollowErrorCode.NOT_FOLLOWING);
            } else {
                // 1-2-2. DB에 팔로우 정보가 존재하는 경우 pendingUnfollows 목록에 언팔로우 요청을 추가
                redisTemplate.opsForSet().add(PENDING_UNFOLLOWS, pendingUnfollowKey);
            }
        }
        return "언팔로우 성공";
    }


    /**
     * 사용자의 팔로잉 목록 조회
     * @param username 사용자 username
     * @return 팔로잉 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowings(String username) {
        Member member = findMemberByUsername(username);
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        // 팔로잉 목록에 대한 Redis 키 생성
        String followingKey = String.format(FOLLOWING_PREFIX, username);

        // Redis 에서 해당 사용자의 팔로잉 목록을 조회
        Set<String> followingIds = setOps.members(followingKey);

        // 1. Redis 에 팔로잉 목록이 이미 있으면, Redis 에서 해당 데이터를 바로 반환
        if (followingIds != null && !followingIds.isEmpty()) {
            return followingIds.stream()
                    .map(this::findMemberByUsername) // username → Member 조회
                    .map(following -> toFollowResponseDto(new Follow(member, following), false))
                    .toList();
        }

        // 2. Redis 에 팔로잉 목록이 없으면, DB 에서 해당 데이터를 조회 후 Redis 에 저장
        List<Follow> followings = followRepository.findByFollower(member);

        // DB 에서 팔로잉 목록을 조회한 후 Redis 에 저장
        if (!followings.isEmpty()) {
            setOps.add(followingKey, followings.stream()
                    .map(f -> f.getFollowing().getUsername()).distinct().toArray(String[]::new)); // Redis 에 저장
        }


        // DB 에서 조회된 팔로잉 목록을 FollowResponseDto 로 변환하여 반환
        return followings.stream()
                .map(f -> toFollowResponseDto(f, false))
                .toList();
    }

    /**
     * 사용자의 팔로워 목록 조회
     * @param username 조회할 회원 username
     * @return 팔로워 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowers(String username) {
        Member member = findMemberByUsername(username);
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        // 팔로워 목록에 대한 Redis 키 생성
        String followersKey = String.format(FOLLOWERS_PREFIX, username);

        // Redis 에서 해당 사용자의 팔로워 목록을 조회
        Set<String> followerIds = setOps.members(followersKey);

        // 1. Redis 에 팔로워 목록이 이미 있으면, Redis 에서 해당 데이터를 바로 반환
        if (followerIds != null && !followerIds.isEmpty()) {
            return followerIds.stream()
                    .map(this::findMemberByUsername) // username → Member 조회
                    .map(follower -> toFollowResponseDto(new Follow(follower, member), true))
                    .toList();
        }

        // 2. Redis 에 팔로워 목록이 없으면, DB 에서 해당 데이터를 조회 후 Redis 에 저장
        List<Follow> followers = followRepository.findByFollowing(member);

        if (!followers.isEmpty()) {
            setOps.add(followersKey, followers.stream()
                    .map(f -> f.getFollower().getUsername()).distinct().toArray(String[]::new)); // Redis 에 저장
        }

        // DB 에서 조회된 팔로워 목록을 FollowResponseDto 로 변환하여 반환
        return followers.stream()
                .map(f -> toFollowResponseDto(f, true))
                .toList();
    }

    /**
     * 회원 username 으로 회원 조회
     * @param username 회원 username
     * @return Member 엔티티
     */
    private Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new FollowException(FollowErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * Follow 엔티티를 FollowResponseDto로 변환
     * @param follow 팔로우 엔티티
     * @param isFollowerList 팔로워 리스트인지 여부
     * @return FollowResponseDto
     */
    private FollowResponseDto toFollowResponseDto(Follow follow, boolean isFollowerList) {
        Member target = isFollowerList ? follow.getFollower() : follow.getFollowing();

        // Redis 에서 followerCount, followingCount 가져오기
        String followersKey = String.format(FOLLOWERS_PREFIX, target.getUsername());
        String followingKey = String.format(FOLLOWING_PREFIX, target.getUsername());

        Long followerCount = redisTemplate.opsForSet().size(followersKey);  // 팔로워 수
        Long followingCount = redisTemplate.opsForSet().size(followingKey); // 팔로잉 수

        return new FollowResponseDto(
                target.getUsername(),
                target.getNickname(),
                followerCount != null ? followerCount : 0,   // null 체크 후 반환
                followingCount != null ? followingCount : 0 // null 체크 후 반환
        );
    }
}
