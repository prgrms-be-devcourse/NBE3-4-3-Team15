package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.*;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 관련 요청을 처리하는 컨트롤러
 * - 회원가입, 로그인, 회원정보 조회 및 수정, 탈퇴, 비밀번호 변경 기능 제공
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     *
     * @param memberDto 회원가입 요청 데이터
     * @return 성공 메시지
     *
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping
    public ResponseEntity<GenericResponse<String>> join(
            @RequestBody @Valid MemberDto memberDto) {
        memberService.join(memberDto);
        return ResponseEntity.ok(GenericResponse.of("회원가입 성공"));
    }

    /**
     * 로그인 (JWT 발급)
     *
     * @param loginDto 로그인 요청 데이터
     * @return JWT 토큰
     *
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<String>> login(
            @RequestBody @Valid LoginDto loginDto) {
        String token = memberService.login(loginDto); // JWT 토큰 발급

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token) // 헤더에 JWT 추가
                .body(GenericResponse.of("로그인 성공")); // body에는 성공 메시지만 반환
    }

    /**
     * 로그아웃
     * @param token 요청 헤더에 포함된 JWT 토큰
     * @return 로그아웃 성공 메시지
     *
     * @author 이원재
     * @since 2025.02.06
     */
    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<String>> logout(
            @RequestHeader("Authorization") String token) {
        // 클라이언트가 JWT를 삭제하도록 응답
        return ResponseEntity.ok(GenericResponse.of("로그아웃 성공"));
    }

    /**
     * 내 정보 조회
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 회원 정보
     *
     * @author 손진영
     * @since 2025.01.27
     */
    @GetMapping("/mine")
    public ResponseEntity<GenericResponse<MemberDto>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberDto myProfile = memberService.getMyProfile(userDetails.getUsername());

        return ResponseEntity.ok(GenericResponse.of(myProfile, "회원 정보 조회 성공"));
    }

    /**
     * 회원 정보 수정
     *
     * @param mineDto 수정할 정보
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 수정된 회원 정보

     * @author 손진영
     * @since 2025.01.28
     */
    @PutMapping("/mine")
    public ResponseEntity<GenericResponse<MemberDto>> updateMyProfile(
            @RequestBody @Valid MineDto mineDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberDto updatedProfile = memberService.modify(userDetails.getUsername(), mineDto);

        return ResponseEntity.ok(GenericResponse.of(updatedProfile, "회원 정보 수정 성공"));
    }

    /**
     * 회원 탈퇴 (비밀번호 확인 후 탈퇴)
     *
     * @param passwordDto 비밀번호 검증 요청 데이터
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 성공 메시지
     *
     * @author 손진영
     * @since 2025.01.31
     */
    @DeleteMapping("/mine")
    public ResponseEntity<GenericResponse<String>> delete(
            @RequestBody @Valid PasswordDto passwordDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.delete(userDetails.getUsername(), passwordDto.getPassword());

        return ResponseEntity.ok(GenericResponse.of("회원 탈퇴가 완료되었습니다."));
    }

    /**
     * 비밀번호 변경
     *
     * @param passwordChangeDto 비밀번호 변경 요청 데이터
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 성공 메시지
     *
     * @author 이원재
     * @since 2025.02.06
     */
    @PutMapping("/mine/password")
    public ResponseEntity<GenericResponse<String>> changePassword(
            @RequestBody @Valid PasswordChangeDto passwordChangeDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.changePassword(userDetails.getUsername(), passwordChangeDto);

        return ResponseEntity.ok(GenericResponse.of("비밀번호 변경 성공"));
    }
}
