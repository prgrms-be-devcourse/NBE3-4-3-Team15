package com.project.backend.domain.challenge.challenge.controller;

import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.dto.DepositDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 챌린지 컨트롤러
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Tag(name = "ChallengeController", description = "챌린지 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
@SecurityRequirement(name = "bearerAuth")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final MemberService memberService;

    /**
     * 챌린지 목록 조회
     *
     * @param status 챌린지 상태 (기본값: WAITING)
     * @return 챌린지 목록
     */
    @GetMapping
    public ResponseEntity<GenericResponse<Page<ChallengeDto>>> items(
            @RequestParam(name = "status", defaultValue = "WAITING") Challenge.ChallengeStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<ChallengeDto> challenges = challengeService.findByStatus(status, page, size);

        return ResponseEntity.ok(GenericResponse.of(
                challenges,
                "챌린지 목록 조회 성공"
        ));
    }

    /**
     * 챌린지 상세 조회
     *
     * @param id 챌린지 ID
     * @return 챌린지 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<ChallengeDto>> item(
            @PathVariable long id
    ) {
        Challenge challenge = challengeService.getChallenge(id);

        return ResponseEntity.ok(GenericResponse.of(
                new ChallengeDto(challenge),
                "챌린지 조회 성공"
        ));
    }

    /**
     * 챌린지 생성
     *
     * @param challengeDto 챌린지 정보
     * @param user         인증된 사용자 정보
     * @return 생성된 챌린지 정보
     */
    @PostMapping("/create")
    public ResponseEntity<GenericResponse<ChallengeDto>> create(
            @RequestBody @Valid ChallengeDto challengeDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (!user.getUsername().equals("admin")) {
            throw new ChallengeException(
                    ChallengeErrorCode.CREATE_CHALLENGE.getStatus(),
                    ChallengeErrorCode.CREATE_CHALLENGE.getErrorCode(),
                    ChallengeErrorCode.CREATE_CHALLENGE.getMessage()
            );
        }

        Challenge challenge = challengeService.create(challengeDto);

        return ResponseEntity.ok(
                GenericResponse.of(
                        new ChallengeDto(challenge),
                        "챌린지 생성 성공"
                )
        );
    }

    /**
     * 챌린지 참가
     *
     * @param id         챌린지 ID
     * @param user       인증된 사용자 정보
     * @param depositDto 예치금 정보
     * @return 참가한 챌린지 정보
     */
    @PostMapping("{id}/join")
    @Transactional
    public ResponseEntity<GenericResponse<ChallengeDto>> join(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DepositDto depositDto
    ) {
        Member member = memberService.getMemberByUsername(user.getUsername());
        Challenge challenge = challengeService.join(id, member, depositDto.getDeposit());

        return ResponseEntity.ok(GenericResponse.of(
                new ChallengeDto(challenge),
                "%s 챌린지 참가 완료".formatted(challenge.getName())
        ));
    }

    /**
     * 챌린지 참가 취소
     *
     * @param id   챌린지 ID
     * @param user 인증된 사용자 정보
     * @return 참가 취소한 챌린지 정보
     */
    @DeleteMapping("{id}/join")
    @Transactional
    public ResponseEntity<GenericResponse<ChallengeDto>> quit(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Member member = memberService.getMemberByUsername(user.getUsername());
        Challenge challenge = challengeService.quit(id, member);

        return ResponseEntity.ok(GenericResponse.of(
                new ChallengeDto(challenge),
                "%s 챌린지 취소 완료".formatted(challenge.getName())
        ));
    }

    /**
     * 챌린지 인증
     *
     * @param id   챌린지 ID
     * @param user 인증된 사용자 정보
     * @return 인증된 챌린지 정보
     */
    @PostMapping("{id}/validation")
    @Transactional
    public ResponseEntity<GenericResponse<ChallengeDto>> validation(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Challenge challenge = challengeService.validation(id, user);

        return ResponseEntity.ok(GenericResponse.of(
                new ChallengeDto(challenge),
                "%s 챌린지 인증 성공".formatted(challenge.getName())
        ));
    }
}
