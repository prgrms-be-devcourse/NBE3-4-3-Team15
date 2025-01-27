package com.project.backend.domain.member.service;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member join(MemberDto memberDto) {
        if (!memberDto.getPassword1().equals(memberDto.getPassword2())) {
            throw new ServiceException(400, "비밀번호 확인에 실패하였습니다.");
        }

        getMember(memberDto.getId())
                .ifPresent((member) -> {
                    throw new ServiceException(409, "이미 존재하는 아이디 입니다.");
                });

        Member member = Member.builder()
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .password(memberDto.getPassword1())
                .nickname(memberDto.getNickname())
                .gender(memberDto.getGender())
                .birth(memberDto.getBirth())
                .build();

        memberRepository.save(member);

        return member;
    }

    public Optional<Member> getMember(String id) {
        return memberRepository.findById(id);
    }
}
