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

// 기존 DB에 저장하고 삭제하는 코드 / TODO: 추후 삭제 예정
//    /**
//     * 팔로우 / 언팔로우 기능
//     * @param followerId 팔로우를 요청하는 회원 ID
//     * @param followingId 팔로우 대상 회원 ID
//     */
//    public String followOrUnfollow(String followerId, String followingId) {
//        Member follower = findMemberByUsername(followerId);
//        Member following = findMemberByUsername(followingId);
//
//        // 자기 자신을 팔로우하려는 경우 예외 처리
//        if (follower.equals(following)) {
//            throw new FollowException(FollowErrorCode.CAN_NOT_FOLLOW_MYSELF);
//        }
//
//        // 이미 팔로우 중인지 확인
//        Follow existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
//        if (existingFollow != null) {
//            // 이미 팔로우 중이면 언팔로우
//            followRepository.delete(existingFollow);
//            return "언팔로우 성공";
//        } else {
//            // 팔로우 등록
//            followRepository.save(new Follow(follower, following));
//            return "팔로우 성공";
//        }
//    }
    /**
     * 팔로우 / 언팔로우 기능
     *
     * @param followerId  팔로우를 요청하는 사용자 ID
     * @param followingId 팔로우 대상 사용자 ID
     * @return 팔로우 또는 언팔로우 성공 메시지
     */
    public String followOrUnfollow(String followerId, String followingId) {
        // 팔로우/언팔로우를 요청한 사용자와 대상 사용자를 조회
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
        String pendingFollowKey = followerId + ":" + followingId; // 팔로우/언팔로우 요청을 식별하는 키 (ex: "1:2")

        // 팔로우 상태 확인
        if (Boolean.TRUE.equals(setOps.isMember(followingKey, followingId))) {
            // 기존 팔로우 상태일 경우 -> 언팔로우 처리

            // 1. 기존 `pendingFollows` 목록에 해당 팔로우 요청이 있는 경우 삭제
            if (Boolean.TRUE.equals(setOps.isMember(PENDING_FOLLOWS, pendingFollowKey))) {
                setOps.remove(PENDING_FOLLOWS, pendingFollowKey);
            }

            // 2. 팔로우 관계를 Redis 에서 삭제
            setOps.remove(followingKey, followingId); // 팔로잉 목록에서 제거
            setOps.remove(followersKey, followerId);  // 팔로워 목록에서 제거

            // 3. `pendingUnfollows` 목록에 언팔로우 요청을 추가
            redisTemplate.opsForSet().add(PENDING_UNFOLLOWS, pendingFollowKey);
            return "언팔로우 성공";
        } else {
            // 현재 팔로우 상태가 아닐 경우 -> 팔로우 처리

            // 1. 기존 `pendingUnfollows` 목록에 해당 언팔로우 요청이 있는 경우 삭제
            if (Boolean.TRUE.equals(setOps.isMember(PENDING_UNFOLLOWS, pendingFollowKey))) {
                setOps.remove(PENDING_UNFOLLOWS, pendingFollowKey);
            }

            // 2. 팔로우 관계를 Redis 에 추가
            setOps.add(followingKey, followingId); // 팔로잉 목록에 추가
            setOps.add(followersKey, followerId); // 팔로워 목록에 추가

            // 3. `pendingFollows` 목록에 팔로우 요청을 추가
            redisTemplate.opsForSet().add(PENDING_FOLLOWS, pendingFollowKey);
            return "팔로우 성공";
        }
    }

    /**
     * 사용자의 팔로워 목록 조회
     * @param username 사용자 username
     * @return 팔로잉 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowings(String username) {
        Member member = findMemberByUsername(username);
        return followRepository.findByFollower(member).stream()
                .map(f -> toFollowResponseDto(f, false))
                .toList();
    }

    /**
     * 사용자의 팔로잉 목록 조회
     * @param username 조회할 회원 username
     * @return 팔로워 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowers(String username) {
        Member member = findMemberByUsername(username);
        return followRepository.findByFollowing(member).stream()
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
     * @return FollowResponseDto
     */
    private FollowResponseDto toFollowResponseDto(Follow follow, boolean isFollowerList) {
        Member target = isFollowerList ? follow.getFollower() : follow.getFollowing();
        return new FollowResponseDto(
                target.getUsername(),
                target.getNickname(),
                followRepository.countByFollowing(target),
                followRepository.countByFollower(target)
        );
    }
}
