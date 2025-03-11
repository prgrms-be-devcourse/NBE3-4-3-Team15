package com.project.backend.domain.challenge.entry.service;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.repository.EntryRepository;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 챌린지 참가 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;

    /**
     * 챌린지에 참가
     *
     * @param challenge 챌린지 정보
     * @param member    회원 정보
     * @param deposit   예치금
     */
    public void join(Challenge challenge, Member member, long deposit) {
        try {
            entryRepository.save(
                    Entry.builder()
                            .challenge(challenge)
                            .member(member)
                            .deposit(deposit)
                            .isActive(true)
                            .refundAmount(0)
                            .rewardAmount(0)
                            .refunded(false)
                            .rate(0)
                            .build()
            );
        } catch (Exception e) {
            throw new ChallengeException(
                    ChallengeErrorCode.DUPLICATE_ENTRY.getStatus(),
                    ChallengeErrorCode.DUPLICATE_ENTRY.getErrorCode(),
                    ChallengeErrorCode.DUPLICATE_ENTRY.getMessage()
            );
        }
    }

    /**
     * 종료된 챌린지의 참가 기록 활성화 상태 업데이트
     */
    public void updateIsActiveForEndedChallenges() {
        entryRepository.updateIsActiveForEndedChallenges();
    }

    /**
     * 챌린지 ID로 참가 기록 목록 조회
     *
     * @param challengeId 챌린지 ID
     * @return 참가 기록 목록
     */
    public List<Entry> findByChallengeId(Long challengeId) {
        return entryRepository.findByChallengeId(challengeId);
    }

    /**
     * 챌린지 ID와 회원 ID로 참가 기록 조회
     *
     * @param challengeId 챌린지 ID
     * @param memberId    회원 ID
     * @return 참가 기록 정보
     */
    public Entry findByChallengeIdAndMemberId(Long challengeId, Long memberId) {
        return entryRepository.findByChallengeIdAndMemberId(challengeId, memberId)
                .orElseThrow(() -> new ChallengeException(
                        ChallengeErrorCode.ENTRY_NOT_FOUND.getStatus(),
                        ChallengeErrorCode.ENTRY_NOT_FOUND.getErrorCode(),
                        ChallengeErrorCode.ENTRY_NOT_FOUND.getMessage()
                ));
    }

    /**
     * 회원 ID로 참가 기록 목록 조회
     *
     * @param id 회원 ID
     * @return 참가 기록 목록
     */
    public List<Entry> findByMemberId(Long id) {
        List<Entry> entries = entryRepository.findByMemberId(id);

        if (entries.isEmpty()) {
            throw new ChallengeException(
                    ChallengeErrorCode.ENTRY_NOT_FOUND.getStatus(),
                    ChallengeErrorCode.ENTRY_NOT_FOUND.getErrorCode(),
                    ChallengeErrorCode.ENTRY_NOT_FOUND.getMessage()
            );
        }
        return entries;
    }

    /**
     * 참가 기록 취소 (삭제)
     *
     * @param entry 참가 기록 정보
     */
    public void quit(Entry entry) {
        entryRepository.delete(entry);
    }
}
