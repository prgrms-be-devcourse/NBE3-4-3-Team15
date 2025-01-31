package com.project.backend.domain.follow.service;

import com.project.backend.domain.follow.dto.FollowRequestDto;
import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.exception.FollowErrorCode;
import com.project.backend.domain.follow.exception.FollowException;
import com.project.backend.domain.follow.repository.FollowRepository;
import com.project.backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    // TODO : MemberRepository pr 후 수정
    //private final MemberRepository memberRepository;

    /**
     * 팔로우 / 언팔로우 기능
     * @param followerId 팔로우를 요청하는 회원 ID
     * @param requestDto 팔로우 대상 회원 ID
     */
    public void followOrUnfollow(String followerId, FollowRequestDto requestDto) {
        Member follower = findMemberById(followerId);
        Member following = findMemberById(requestDto.followingId());

        // 자기 자신을 팔로우하려는 경우 예외 처리
        if (follower.equals(following)) {
            throw new FollowException(FollowErrorCode.CAN_NOT_FOLLOW_MYSELF);
        }

        // 이미 팔로우 중인지 확인
        Follow existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow != null) {
            // 이미 팔로우 중이면 언팔로우
            followRepository.delete(existingFollow);
        } else {
            // 팔로우 등록
            Follow follow = new Follow(follower, following);
            followRepository.save(follow);
        }
    }

    /**
     * 사용자의 팔로잉 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 팔로잉 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowings(String memberId) {
        Member member = findMemberById(memberId);
        return followRepository.findFollowingsByMember(member).stream()
                .map(f -> toFollowResponseDto(f, false))
                .toList();
    }

    /**
     * 사용자의 팔로워 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 팔로워 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowers(String memberId) {
        Member member = findMemberById(memberId);
        return followRepository.findFollowersByMember(member).stream()
                .map(f -> toFollowResponseDto(f, true))
                .toList();
    }

    /**
     * 회원 ID로 회원 조회
     * @param memberId 회원 ID
     * @return Member 엔티티
     */
    // TODO : MemberRepository pr 후 수정
    private Member findMemberById(String memberId) {
        return memberRepository.findById(memberId)
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
                target.getId(),
                target.getNickname(),
                followRepository.countFollowers(target),
                followRepository.countFollowings(target)
        );
    }
}
