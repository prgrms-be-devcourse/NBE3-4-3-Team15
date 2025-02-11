package com.project.backend.domain.follow.controller;

import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.service.FollowService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로우 기능을 처리하는 컨트롤러
 * author: 이원재
 * since: 2025.02.07
 */
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * @param username 팔로우/언팔로우를 하고자 하는 회원 username
     * @param userDetails 로그인한 회원의 정보
     * 팔로우 또는 언팔로우
     * @return 팔로우/언팔로우 성공 여부
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<GenericResponse<String>> followOrUnfollow(
            @PathVariable("id") String username,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        System.out.println(username);
        String message = followService.followOrUnfollow(userDetails.getUsername(), username);

        return ResponseEntity.ok(GenericResponse.of(message));
    }

    /**
     * 사용자의 팔로잉 목록 조회
     * (팔로잉 목록 = 내가 팔로우 한 사람의 목록)
     * @param username 요청하는 회원 username
     * @return 회원 ID에 해당하는 팔로잉 목록
     */
    @GetMapping("/{id}/followings")
    public ResponseEntity<GenericResponse<List<FollowResponseDto>>> getFollowings(
            @PathVariable("id") String username) {
        List<FollowResponseDto> followings = followService.getFollowings(username);

        return ResponseEntity.ok(GenericResponse.of(followings, "팔로잉 목록 조회 성공"));
    }

    /**
     * 사용자의 팔로워 목록 조회
     * (팔로워 목록 = 나를 팔로우 한 사람의 목록)
     * @param username 요청하는 회원 username
     * @return 회원 ID에 해당하는 팔로워 목록
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<GenericResponse<List<FollowResponseDto>>> getFollowers(
            @PathVariable("id") String username) {
        List<FollowResponseDto> followers = followService.getFollowers(username);

        return ResponseEntity.ok(GenericResponse.of(followers,"팔로워 목록 조회 성공"));
    }
}
