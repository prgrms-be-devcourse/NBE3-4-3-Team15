package com.project.backend.domain.challenge.challenge.controller;

import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
public class ChallengeController {

    private ChallengeService challengeService;

    @PostMapping("{id}/join")
    @Transactional
    public ResponseEntity<GenericResponse<Void>> join(
            @PathVariable long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody long deposit
            ) {

        challengeService.join(id, user, deposit);

        return ResponseEntity.ok(GenericResponse.of("성공"));
    }
}
