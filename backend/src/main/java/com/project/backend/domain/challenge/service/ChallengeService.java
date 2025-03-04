package com.project.backend.domain.challenge.service;

import com.project.backend.domain.challenge.entity.Challenge;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.challenge.repository.ChallengeRepository;
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

    private ChallengeRepository challengeRepository;
    private MemberService memberService;

    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeException(
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getStatus(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getErrorCode(),
                        ChallengeErrorCode.CHALLENGE_NOT_FOUND.getMessage()
                ));
    }

    public void join(long id, CustomUserDetails user) {
        Challenge challenge = getChallenge(id);

//        challenge.getMembers().add(memberService.getMemberByUsername(user.getName()));
    }
}
