package com.project.backend.domain.challenge.challenge.service;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.challenge.exception.ChallengeException;
import com.project.backend.domain.challenge.challenge.repository.ChallengeRepository;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * 챌린지 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberService memberService;
    private final EntryService entryService;

    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeException(
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getStatus(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getErrorCode(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getMessage()
                ));
    }

    public void join(long id, CustomUserDetails user, long deposit) {
        Challenge challenge = getChallenge(id);
        Member member = memberService.getMemberByUsername(user.getName());

        entryService.join(challenge, member, deposit);
    }
}
