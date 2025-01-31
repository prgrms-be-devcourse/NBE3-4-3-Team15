package com.project.backend.domain.follow.controller;

import com.project.backend.domain.follow.dto.FollowRequestDto;
import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.service.FollowService;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로우 기능을 처리하는 컨트롤러
 * author: 이원재
 * since: 2025.01.31
 */
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우 또는 언팔로우
     * @param memberId 팔로우를 요청하는 회원 ID
     * @param requestDto 팔로우 대상 회원 ID
     * @return 팔로우 성공 여부
     */
    @PostMapping("/{memberId}/follow")
    public ResponseEntity<GenericResponse<Void>> followOrUnfollow(@PathVariable String memberId,
                                                                  @RequestBody FollowRequestDto requestDto) {
        followService.followOrUnfollow(memberId, requestDto);
        return ResponseEntity.ok(GenericResponse.of());
    }

    /**
     * 사용자의 팔로잉 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 팔로잉 목록
     */
    @GetMapping("/{memberId}/followings")
    public ResponseEntity<GenericResponse<List<FollowResponseDto>>> getFollowings(@PathVariable String memberId) {
        List<FollowResponseDto> followings = followService.getFollowings(memberId);
        return ResponseEntity.ok(GenericResponse.of(followings));
    }

    /**
     * 사용자의 팔로워 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 팔로워 목록
     */
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<GenericResponse<List<FollowResponseDto>>> getFollowers(@PathVariable String memberId) {
        List<FollowResponseDto> followers = followService.getFollowers(memberId);
        return ResponseEntity.ok(GenericResponse.of(followers));
    }
}
