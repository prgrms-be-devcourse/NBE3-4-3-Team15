package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.*;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberErrorCode;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.project.backend.domain.member.exception.MemberErrorCode.INCORRECT_AUTHORIZED;

/**
 * 회원 컨트롤러
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입 요청
     *
     * @param memberDto
     * @return GenericResponse<MemberDto>
     * @Valid
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping
    public GenericResponse<MemberDto> join(@RequestBody @Valid MemberDto memberDto) {
        Member member = memberService.join(memberDto);

        return GenericResponse.of("회원가입 성공");
    }

    /**
     * 로그인 요청
     *
     * @param loginDto
     * @return GenericResponse<MemberDto>
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping("/login")
    public GenericResponse<String> login(@RequestBody @Valid LoginDto loginDto) {
        String token = memberService.login(loginDto); // JWT 토큰 발급
        return GenericResponse.of(
                token,
                "로그인 성공");
    }

    /**
     * 회원 정보 조회
     *
     * @return GenericResponse<MemberDto>
     * @author 손진영
     * @since 2025.01.27
     */
    @GetMapping("/mine")
    public GenericResponse<MemberDto> mine() {

        Member member = getAuthenticatedMember();

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 조회 성공"
        );
    }

    /**
     * 회원 정보 수정
     *
     * @param mineDto
     * @return GenericResponse<MemberDto>
     * @Valid
     * @author 손진영
     * @since 2025.01.28
     */
    @PutMapping("/mine")
    @Transactional
    public GenericResponse<MemberDto> mine(@RequestBody @Valid MineDto mineDto) {

        Member member = getAuthenticatedMember();

        memberService.modify(member,
                mineDto.getEmail(),
                mineDto.getGender(),
                mineDto.getNickname(),
                mineDto.getBirth());

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 수정 성공"
        );
    }

    /**
     * 회원 탈퇴
     *
     * @param passwordDto
     * @return GenericResponse
     * @Valid
     * @author 손진영
     * @since 2025.01.31
     */
    @DeleteMapping("/mine")
    public GenericResponse mine(@RequestBody @Valid PasswordDto passwordDto) {
        Member member = getAuthenticatedMember();

        if (!passwordDto.getPassword().equals(member.getPassword()))
            throw new MemberException(INCORRECT_AUTHORIZED);

        memberService.delete(member,passwordDto.getPassword());

        SecurityContextHolder.clearContext();

        return GenericResponse.of("탈퇴 성공");
    }

    /**
     * 현재 로그인한 사용자 가져오기
     */
    private Member getAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberService.getMember(authentication.getName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.INCORRECT_AUTHORIZED));
    }


    /**
     * 비밀번호 변경
     *
     * @param passwordChangeDto
     * @return GenericResponse
     * @Valid
     */
    @PutMapping("/mine/password")
    public GenericResponse changePassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        Member member = getAuthenticatedMember();
        memberService.changePassword(member, passwordChangeDto);
        return GenericResponse.of("비밀번호 변경 성공");
    }
}
