package com.project.backend.domain.challenge.entry.controller;

import com.project.backend.domain.challenge.entry.dto.EntryDto;
import com.project.backend.domain.challenge.entry.dto.RefundsDto;
import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.challenge.entry.service.EntryService;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 엔트리 컨트롤러
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Tag(name = "EntryController", description = "엔트리 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge/entry")
@SecurityRequirement(name = "bearerAuth")
public class EntryController {

    private final MemberService memberService;
    private final EntryService entryService;

    /**
     * 챌린지 ID로 참가 기록 조회
     *
     * @param user 인증된 사용자 정보
     * @param id   챌린지 ID
     * @return 참가 기록 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<EntryDto>> item(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable long id
    ) {
        Member member = memberService.getMemberByUsername(user.getUsername());
        Entry entry = entryService.findByChallengeIdAndMemberId(id, member.getId());


        return ResponseEntity.ok(GenericResponse.of(
                new EntryDto(entry),
                "해당 챌린지 참가 확인"
        ));
    }

    /**
     * 회원의 참가 기록 목록 조회
     *
     * @param user 인증된 사용자 정보
     * @return 회원의 참가 기록 목록
     */
    @GetMapping("/mine")
    public ResponseEntity<GenericResponse<Page<EntryDto>>> mine(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Member member = memberService.getMemberByUsername(user.getUsername());
        Page<EntryDto> entryDtos = entryService.findByMemberId(member.getId(), page, size);

        return ResponseEntity.ok(GenericResponse.of(
                entryDtos,
                "내 챌린지 목록 조회 성공"
        ));
    }

    /**
     * 회원의 환급 목록 조회
     *
     * @param user 인증된 사용자 정보
     * @return 회원의 환급 목록
     */
    @GetMapping("/mine/refunds")
    public ResponseEntity<GenericResponse<Page<RefundsDto>>> myRefunds(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Member member = memberService.getMemberByUsername(user.getUsername());
        Page<RefundsDto> refundsDtos = entryService.findRefundsByMemberId(member.getId(), page, size);

        return ResponseEntity.ok(GenericResponse.of(
                refundsDtos,
                "내 환급 목록 조회 성공"
        ));
    }
}
