package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final HttpServletRequest request;

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

    @GetMapping("/mine")
    public MemberDto mine() {

        String authorization = request.getHeader("Authorization");
        String apiKey = authorization == null ? "" : authorization.substring("Bearer ".length());

        if (apiKey.isEmpty()) throw new ServiceException(401, "인증정보가 없습니다.");

        Member member = memberService.getMember(apiKey)
                .orElseThrow(() -> new ServiceException(401, "인증정보가 올바르지 않습니다."));

        return new MemberDto(member);
    }
}
