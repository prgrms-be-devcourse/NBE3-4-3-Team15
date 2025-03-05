package com.project.backend.domain.challenge.challenge.controller;

import com.project.backend.domain.challenge.challenge.dto.ChallengeDto;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 *
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

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<ChallengeDto>> create(
            @RequestBody ChallengeDto challengeDto
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
    public ResponseEntity<GenericResponse<Void>> join(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody long deposit
            ) {

        challengeService.join(id, user, deposit);

        return ResponseEntity.ok(GenericResponse.of("챌린지 참가 완료"));
    }

    @PostMapping("{id}/validation")
    public ResponseEntity<GenericResponse<Void>> validation(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Challenge challenge = challengeService.validation(id, user);

        return ResponseEntity.ok(GenericResponse.of("%s 챌린지 인증 성공".formatted(challenge.getName())));
    }
}
