package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.response.GenericResponse;
import com.project.backend.global.exception.GlobalErrorCode;
import com.project.backend.global.exception.GlobalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
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
     * @Valid
     * @return GenericResponse<MemberDto>
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
     * author 손진영
     * since 2025.01.27
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
     * author 손진영
     * since 2025.01.27
     */
    @PostMapping("/login")
    public GenericResponse<MemberDto> login(@RequestBody @Valid LoginReqBody reqBody) {
        Member member = memberService.getMember(reqBody.id)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NON_EXISTING_ID));

        if (!member.getPassword().equals(reqBody.password))
            throw new GlobalException(GlobalErrorCode.INCORRECT_PASSWORD);

        return GenericResponse.of(
                new MemberDto(member),
                "로그인 성공"
        );
    }
}
