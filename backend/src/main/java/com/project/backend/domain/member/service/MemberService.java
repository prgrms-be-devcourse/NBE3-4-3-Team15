package com.project.backend.domain.member.service;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.global.exception.GlobalErrorCode;
import com.project.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.project.backend.global.exception.GlobalErrorCode.EXISTING_ID;
import static com.project.backend.global.exception.GlobalErrorCode.INVALID_PASSWORD;

/**
 *
 * 회원 Service
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


    /**
     * 회원가입 처리
     *
     * @param memberDto
     * @return Member
     * @throws GlobalException INVALID_PASSWORD: 입력된 두 비밀번호가 일치하지 않는 경우.
     *                         EXISTING_ID: 이미 존재하는 아이디로 회원가입을 시도한 경우.
     * @author 손진영
     * @since 25. 1. 27.
     */
    public Member join(MemberDto memberDto) throws GlobalException {
        if (!memberDto.getPassword1().equals(memberDto.getPassword2())) {
            throw new GlobalException(INVALID_PASSWORD);
        }

        getMember(memberDto.getId())
                .ifPresent((member) -> {
                    throw new GlobalException(EXISTING_ID);
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

    /**
     * 회원 정보 조회
     *
     * @param id
     * @return Optional<Member>
     * @author 손진영
     * @since 25. 1. 27.
     */
    public Optional<Member> getMember(String id) {
        return memberRepository.findById(id);
    }

    /**
     * 회원 정보 수정
     *
     * @param member
     * @param password
     * @param email
     * @param gender
     * @param nickname
     * @param birth
     * @author 손진영
     * @since 25. 1. 28.
     */
    public void modify(Member member, String password, String email, int gender, String nickname, LocalDate birth) {
        if (!password.isEmpty()) {
            if (password.length() < 8) throw new GlobalException(GlobalErrorCode.PASSWORD_LENGTH);
            member.setPassword(password);
        }
        member.setEmail(email);
        member.setGender(gender);
        member.setNickname(nickname);
        member.setBirth(birth);
    }
}
