package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.exception.GlobalErrorCode;
import com.project.backend.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.web.bind.annotation.*;

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
     * @Valid
     * @return MemberDto
     * author 손진영
     * since 2025.01.27
     */
    @PostMapping
    public MemberDto join(@RequestBody @Valid MemberDto memberDto) {
        Member member = memberService.join(memberDto);
        return new MemberDto(member);
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
     * @return MemberDto
     * author 손진영
     * since 2025.01.27
     */
    @PostMapping("/login")
    public MemberDto login(@RequestBody @Valid LoginReqBody reqBody) {
        Member member = memberService.getMember(reqBody.id)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NON_EXISTING_ID));

        if (!member.getPassword().equals(reqBody.password))
            throw new GlobalException(GlobalErrorCode.INCORRECT_PASSWORD);

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
