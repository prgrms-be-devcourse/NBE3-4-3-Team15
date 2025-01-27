package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public MemberDto join(@RequestBody @Valid MemberDto memberDto) {
        Member member = memberService.join(memberDto);
        return new MemberDto(member);
    }
}
