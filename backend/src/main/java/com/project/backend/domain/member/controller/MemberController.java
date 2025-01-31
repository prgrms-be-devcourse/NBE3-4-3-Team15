package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.project.backend.domain.member.exception.MemberErrorCode.*;

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
    private final HttpServletRequest request;

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

        return GenericResponse.of(
                new MemberDto(member),
                "회원가입 성공"
        );
    }

    /**
     * 로그인 요청 레코드
     *
     * @param id
     * @param password
     * @author 손진영
     * @since 2025.01.27
     */
    record LoginReqBody(
            @NotBlank
            String id,
            @NotBlank
            String password
    ) {
    }

    /**
     * 로그인 요청
     *
     * @param reqBody
     * @return GenericResponse<MemberDto>
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping("/login")
    public GenericResponse<MemberDto> login(@RequestBody @Valid LoginReqBody reqBody) {
        Member member = memberService.getMember(reqBody.id)
                .orElseThrow(() -> new MemberException(NON_EXISTING_ID));

        if (!member.getPassword().equals(reqBody.password))
            throw new MemberException(INCORRECT_PASSWORD);

        return GenericResponse.of(
                new MemberDto(member),
                "로그인 성공"
        );
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

        Member member = checkAuthMember();

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 조회 성공"
        );
    }

    /**
     * 회원 정보 수정 레코드
     *
     * @param password
     * @param email
     * @param gender
     * @param nickname
     * @param birth
     * @author 손진영
     * @since 2025.01.28
     */
    record MineReqBody(
            String password,
            @NotBlank
            @Length(max = 25)
            @Email
            String email,
            int gender,
            @NotBlank
            @Length(min = 2, max = 20)
            String nickname,
            LocalDate birth
    ) {
    }

    /**
     * 회원 정보 수정
     *
     * @param reqBody
     * @return GenericResponse<MemberDto>
     * @Valid
     * @author 손진영
     * @since 2025.01.28
     */
    @PutMapping("/mine")
    @Transactional
    public GenericResponse<MemberDto> mine(@RequestBody @Valid MineReqBody reqBody) {

        Member member = checkAuthMember();

        memberService.modify(member, reqBody.password, reqBody.email, reqBody.gender, reqBody.nickname, reqBody.birth);

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 수정 성공"
        );
    }

    /**
     * 회원 탈퇴 레코드
     *
     * @param password
     * @author 손진영
     * @since 2025.01.28
     */
    record QuitReqBody(
         @NotBlank
         String password
    ){}

    /**
     * 회원 탈퇴
     *
     * @param reqBody
     * @return GenericResponse
     * @Valid
     * @author 손진영
     * @since 2025.01.31
     */
    @DeleteMapping("/mine")
    public GenericResponse mine(@RequestBody @Valid QuitReqBody reqBody) {
        Member member = checkAuthMember();

        if (!reqBody.password.equals(member.getPassword()))
            throw new MemberException(INCORRECT_AUTHORIZED);

        memberService.delete(member);

        return GenericResponse.of("탈퇴 성공");
    }

    /**
     * Request Authorization Check
     *
     * @return Member
     * @author 손진영
     * @since 2025.01.31
     */
    private Member checkAuthMember() {
        String authorization = request.getHeader("Authorization");
        String apiKey = authorization == null ? "" : authorization.substring("Bearer ".length());

        if (apiKey.isEmpty()) throw new MemberException(NO_AUTHORIZED);

        return memberService.getMember(apiKey)
                .orElseThrow(() -> new MemberException(INCORRECT_AUTHORIZED));
    }
}
