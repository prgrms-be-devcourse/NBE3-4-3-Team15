package com.project.backend.domain.challenge.challenge.controller;

import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.dto.DepositDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 챌린지 컨트롤러
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Tag(name = "ChallengeController", description = "챌린지 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
@SecurityRequirement(name = "bearerAuth")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<ChallengeDto>>> items(
            @RequestParam(name = "status", defaultValue = "WAITING") Challenge.ChallengeStatus status
    ) {
        List<ChallengeDto> challenges = challengeService.findByStatus(status);

        return ResponseEntity.ok(GenericResponse.of(
                challenges,
                "챌린지 목록 조회 성공"
        ));
    }

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

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<ChallengeDto>> create(
            @RequestBody @Valid ChallengeDto challengeDto
    ) {
        Challenge challenge = challengeService.create(challengeDto);

        return ResponseEntity.ok(
                GenericResponse.of(
                        new ChallengeDto(challenge),
                        "챌린지 생성 성공"
                )
        );
    }

    @PostMapping("{id}/join")
    @Transactional
    public ResponseEntity<GenericResponse<ChallengeDto>> join(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DepositDto depositDto
    ) {

        Challenge challenge = challengeService.join(id, user, depositDto.getDeposit());

        return ResponseEntity.ok(GenericResponse.of(
                new ChallengeDto(challenge),
                "%s 챌린지 참가 완료".formatted(challenge.getName())
        ));
    }

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
