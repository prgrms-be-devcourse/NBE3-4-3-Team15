package com.project.backend.domain.follow.scheduler;

import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.repository.FollowRepository;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowSyncScheduler {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PENDING_FOLLOWS = "pendingFollows";
    private static final String PENDING_UNFOLLOWS = "pendingUnfollows";

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void syncFollowData() {
        log.info("[FollowSyncScheduler] Redis와 DB 동기화 시작");
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        // 1. pendingFollows 처리 (새로운 팔로우 관계 추가)
        Set<String> followRequests = setOps.members(PENDING_FOLLOWS);
        if (followRequests != null) {
            for (String request : followRequests) {
                String[] ids = request.split(":");
                String followerId = ids[0];
                String followingId = ids[1];

                Member follower = memberRepository.findByUsername(followerId).orElse(null);
                Member following = memberRepository.findByUsername(followingId).orElse(null);

                if (follower != null && following != null) {
                    if (followRepository.findByFollowerAndFollowing(follower, following) == null) {
                        followRepository.save(new Follow(follower, following));
                    }
                }
                setOps.remove(PENDING_FOLLOWS, request); // 처리된 요청 제거
            }
        }

        // 2. pendingUnfollows 처리 (언팔로우 관계 삭제)
        Set<String> unfollowRequests = setOps.members(PENDING_UNFOLLOWS);
        if (unfollowRequests != null) {
            for (String request : unfollowRequests) {
                String[] ids = request.split(":");
                String followerId = ids[0];
                String followingId = ids[1];

                Member follower = memberRepository.findByUsername(followerId).orElse(null);
                Member following = memberRepository.findByUsername(followingId).orElse(null);

                if (follower != null && following != null) {
                    Follow follow = followRepository.findByFollowerAndFollowing(follower, following);
                    if (follow != null) {
                        followRepository.delete(follow);
                    }
                }
                setOps.remove(PENDING_UNFOLLOWS, request); // 처리된 요청 제거
            }
        }

        log.info("[FollowSyncScheduler] Redis와 DB 동기화 완료");
    }
}