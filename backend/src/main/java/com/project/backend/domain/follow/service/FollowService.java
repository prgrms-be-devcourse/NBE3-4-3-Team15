package com.project.backend.domain.follow.service;

import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.exception.FollowErrorCode;
import com.project.backend.domain.follow.exception.FollowException;
import com.project.backend.domain.follow.repository.FollowRepository;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 팔로우 / 언팔로우 기능
     * @param followerId 팔로우를 요청하는 회원 ID
     * @param followingId 팔로우 대상 회원 ID
     */
    public String followOrUnfollow(String followerId, String followingId) {
        Member follower = findMemberByUsername(followerId);
        Member following = findMemberByUsername(followingId);

        // 자기 자신을 팔로우하려는 경우 예외 처리
        if (follower.equals(following)) {
            throw new FollowException(FollowErrorCode.CAN_NOT_FOLLOW_MYSELF);
        }

        // 이미 팔로우 중인지 확인
        Follow existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow != null) {
            // 이미 팔로우 중이면 언팔로우
            followRepository.delete(existingFollow);
            return "언팔로우 성공";
        } else {
            // 팔로우 등록
            followRepository.save(new Follow(follower, following));
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
