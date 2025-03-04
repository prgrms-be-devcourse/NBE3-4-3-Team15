package com.project.backend.domain.challenge.controller;

import com.project.backend.domain.challenge.service.ChallengeService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @AuthenticationPrincipal CustomUserDetails user
            ) {

        challengeService.join(id, user);

        return ResponseEntity.ok(GenericResponse.of("성공"));
    }
}
