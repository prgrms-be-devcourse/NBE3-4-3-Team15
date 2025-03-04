package com.project.backend.domain.challenge.entry.service;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.repository.EntryRepository;
import com.project.backend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * 챌린지 참가 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;

    public void join(Challenge challenge, Member member, long deposit) {
        entryRepository.save(
                Entry.builder()
                        .challenge(challenge)
                        .member(member)
                        .deposit(deposit)
                        .isActive(true)
                        .build()
        );
    }
}
