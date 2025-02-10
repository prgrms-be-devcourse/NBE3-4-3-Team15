package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.*;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 관련 요청을 처리하는 컨트롤러
 * - 회원가입, 로그인, 회원정보 조회 및 수정, 탈퇴, 비밀번호 변경 기능 제공
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@Tag(name = "MemberController", description = "회원 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "회원가입")
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
    @Operation(summary = "로그인")
    public ResponseEntity<GenericResponse<String>> login(
            @RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
        String token = memberService.login(loginDto); // JWT 토큰 발급

        // JWT를 Set-Cookie로 저장 (HttpOnly, Secure 옵션을 설정하여 보안 강화)
        response.addHeader("Set-Cookie", "accessToken=" + token + "; HttpOnly; Path=/; Max-Age=3600; SameSite=Strict");

        return ResponseEntity.ok(GenericResponse.of("로그인 성공"));
    }
    /**
     * 로그아웃
     * @param response
     * @return 로그아웃 성공 메시지
     *
     * @author 이원재
     * @since 2025.02.06
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<GenericResponse<String>> logout(
            HttpServletResponse response) {
        // 로그아웃 시 쿠키에서 JWT를 삭제
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 쿠키를 삭제
        response.addCookie(cookie);

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
    @Operation(summary = "회원 정보 조회")
    public ResponseEntity<GenericResponse<MineDto>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MineDto myProfile = memberService.getMyProfile(userDetails.getUsername());

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
    @Transactional
    @Operation(summary = "회원 정보 수정")
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
    @Operation(summary = "회원 탈퇴")
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
    @Operation(summary = "비밀번호 변경")
    public ResponseEntity<GenericResponse<String>> changePassword(
            @RequestBody @Valid PasswordChangeDto passwordChangeDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.changePassword(userDetails.getUsername(), passwordChangeDto);

        return ResponseEntity.ok(GenericResponse.of("비밀번호 변경 성공"));
    }
}