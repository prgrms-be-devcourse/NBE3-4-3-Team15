package com.project.backend.domain.member.service;

import com.project.backend.domain.member.dto.LoginDto;
import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.global.exception.GlobalException;
import com.project.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.project.backend.domain.member.exception.MemberErrorCode.*;

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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
            throw new MemberException(INVALID_PASSWORD);
        }

        getMember(memberDto.getUsername())
                .ifPresent((member) -> {
                    throw new MemberException(EXISTING_ID);
                });

        Member member = Member.builder()
                .username(memberDto.getUsername())
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword1())) // 비밀번호 암호화
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
     * @param username
     * @return Optional<Member>
     * @author 손진영
     * @since 25. 1. 27.
     */
    public Optional<Member> getMember(String username) {
        return memberRepository.findByUsername(username);
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
            if (password.length() < 8) throw new MemberException(PASSWORD_LENGTH);
            member.setPassword(password);
        }
        member.setEmail(email);
        member.setGender(gender);
        member.setNickname(nickname);
        member.setBirth(birth);
    }

    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public String login(LoginDto loginDto) throws GlobalException {
        Member member = memberRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(()->new MemberException(NON_EXISTING_ID));

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) { // 암호화된 비밀번호 비교
            throw new MemberException(INCORRECT_PASSWORD);
        }
        
        return jwtUtil.generateToken(member.getUsername()); // JWT 토큰 발급
    }
}
