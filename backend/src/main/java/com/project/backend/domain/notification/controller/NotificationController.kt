package com.project.backend.domain.notification.controller

import com.project.backend.domain.member.dto.MemberDto
import com.project.backend.domain.member.service.MemberService
import com.project.backend.domain.notification.dto.NotificationDTO
import com.project.backend.domain.notification.service.NotificationService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notification")
class NotificationController(
    private val notificationService: NotificationService,
    private val memberService: MemberService
) {

    /**
     * 알림 생성
     */
    @PostMapping
    fun createNotification(@RequestBody notificationDTO: NotificationDTO): ResponseEntity<GenericResponse<NotificationDTO>> {
        val newNotificationDTO = notificationService.create(notificationDTO)
        return ResponseEntity.ok(GenericResponse.of(newNotificationDTO, "알람 생성 성공"))
    }

    /**
     * 특정 유저 알림 조회
     */
    @GetMapping("/myNotification")
    fun getUserIdNotification(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "false") onlyNotCheck: Boolean
    ): ResponseEntity<GenericResponse<Page<NotificationDTO>>> {
        val memberDto = memberService.getMyProfile(userDetails.username)
        val notificationDTOS = notificationService.findByUser(memberDto, page, size, onlyNotCheck)
        return ResponseEntity.ok(GenericResponse.of(notificationDTOS, "알림 조회 성공"))
    }

    /**
     * 알림 읽음 상태 변경
     */
    @PutMapping("/{notificationId}")
    fun notificationCheck(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<String>> {
        val memberDto = memberService.getMyProfile(userDetails.username)
        notificationService.notificationCheck(notificationId, memberDto)
        return ResponseEntity.ok(GenericResponse.of("변경 성공"))
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    fun notificationDelete(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<String>> {
        val memberDto = memberService.getMyProfile(userDetails.username)
        notificationService.deleteNotification(notificationId, memberDto)
        return ResponseEntity.ok(GenericResponse.of("삭제 성공"))
    }

    /**
     * 전체 알림 개수 조회
     */
    @GetMapping("/{notificationCount}")
    fun notificationTotalCount(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GenericResponse<Long>> {
        val memberDto = memberService.getMyProfile(userDetails.username)
        val total = notificationService.getNotificationTotalCount(memberDto)
        return ResponseEntity.ok(GenericResponse.of(total, "전체 알람 전달 성공"))
    }
}
