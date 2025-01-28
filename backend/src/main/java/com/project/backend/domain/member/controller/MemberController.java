package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.exception.ServiceException;
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
     * @return MemberDto
     */
    @PostMapping
    public MemberDto join(@RequestBody @Valid MemberDto memberDto) {
        Member member = memberService.join(memberDto);
        return new MemberDto(member);
    }

    record LoginReqBody(
            @NotBlank
            String id,
            @NotBlank
            String password
    ) {
    }

    @PostMapping("/login")
    public MemberDto login(@RequestBody @Valid LoginReqBody reqBody) {
        Member member = memberService.getMember(reqBody.id)
                .orElseThrow(() -> new ServiceException(404, "존재하지 않는 사용자 입니다."));

        if (!member.getPassword().equals(reqBody.password)) throw new ServiceException(401, "비밀번호가 맞지 않습니다.");

        return new MemberDto(member);
    }
}
