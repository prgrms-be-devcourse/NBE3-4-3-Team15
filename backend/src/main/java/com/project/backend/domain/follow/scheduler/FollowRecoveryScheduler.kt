package com.project.backend.domain.follow.scheduler;

import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.repository.FollowRepository;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowRecoveryScheduler {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    // Redis 키 패턴 정의 (FollowService와 동일하게 유지)
    private static final String FOLLOWING_PREFIX = "%s:following";
    private static final String FOLLOWERS_PREFIX = "%s:followers";

    /**
     * Redis 서버가 복구되었는지 주기적으로 확인하고, 복구 시 DB 데이터를 Redis에 동기화
     * 1초마다 실행되며, 연결이 끊어졌다가 복구되면 데이터를 동기화함
     */
    @Scheduled(fixedRate = 1000) // 1초마다 실행
    @Transactional(readOnly = true)
    public void checkRedisConnectionAndRecover() {
        try {
            // Redis 연결 확인
            boolean isConnected = redisConnectionFactory.getConnection().ping() != null;

            if (isConnected) {
                // Redis 키 확인을 통해 데이터가 있는지 검사
                if (!hasRedisData()) {
                    log.info("[FollowRecoveryScheduler] Redis 서버가 복구되었고 데이터가 없어 DB 데이터를 동기화합니다.");
                    recoverRedisFromDatabase();
                }
            }
        } catch (Exception e) {
            // 로그 빈도를 줄이기 위해 연결 오류는 debug 레벨로 로깅
            log.debug("[FollowRecoveryScheduler] Redis 연결 확인 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * Redis에 팔로우 관련 데이터가 있는지 확인
     * @return 데이터 존재 여부
     */
    private boolean hasRedisData() {
        try {
            // 샘플 사용자 (임의의 사용자)에 대한 데이터가 있는지 확인
            List<Member> sampleMembers = memberRepository.findAll();

            // 회원이 없는 경우 false 반환
            if (sampleMembers.isEmpty()) {
                return false;
            }

            // 처음 10명의 회원만 확인 (성능 최적화)
            int checkLimit = Math.min(10, sampleMembers.size());

            for (int i = 0; i < checkLimit; i++) {
                Member member = sampleMembers.get(i);
                String username = member.getUsername();

                // 해당 사용자의 팔로잉/팔로워 키가 있는지 확인
                String followingKey = String.format(FOLLOWING_PREFIX, username);
                String followersKey = String.format(FOLLOWERS_PREFIX, username);

                Boolean followingKeyExists = redisTemplate.hasKey(followingKey);
                Boolean followersKeyExists = redisTemplate.hasKey(followersKey);

                // 키가 존재하면 데이터가 있다고 판단
                if (Boolean.TRUE.equals(followingKeyExists) || Boolean.TRUE.equals(followersKeyExists)) {
                    return true;
                }
            }

            // 확인한 모든 사용자에 대해 데이터가 없음
            return false;
        } catch (Exception e) {
            log.error("[FollowRecoveryScheduler] Redis 데이터 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    /**
     * DB의 팔로우 데이터를 Redis로 복구
     */
    private void recoverRedisFromDatabase() {
        log.info("[FollowRecoveryScheduler] DB 데이터를 Redis로 복구 시작");
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        try {
            // 모든 팔로우 관계를 DB에서 조회
            List<Follow> allFollows = followRepository.findAll();

            for (Follow follow : allFollows) {
                Member follower = follow.getFollower();
                Member following = follow.getFollowing();

                if (follower != null && following != null) {
                    String followerId = follower.getUsername();
                    String followingId = following.getUsername();

                    // Redis 키 설정
                    String followingKey = String.format(FOLLOWING_PREFIX, followerId);  // 팔로잉 목록
                    String followersKey = String.format(FOLLOWERS_PREFIX, followingId); // 팔로워 목록

                    // Redis에 팔로우 관계 데이터 추가
                    setOps.add(followingKey, followingId);
                    setOps.add(followersKey, followerId);
                }
            }

            log.info("[FollowRecoveryScheduler] 총 {}개의 팔로우 관계를 Redis에 복구 완료", allFollows.size());
        } catch (Exception e) {
            log.error("[FollowRecoveryScheduler] Redis 데이터 복구 중 오류 발생: {}", e.getMessage());
        }
    }
}