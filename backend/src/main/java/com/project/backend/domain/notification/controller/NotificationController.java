package com.project.backend.domain.notification.controller;


import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알람 컨트롤러
 *
 * @author 이광석
 * @since 25.02.06
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final MemberService memberService;


    /**
     * 알림 생성
     * @param notificationDTO
     * @return ResponseEntity<GenericResponse<NotificationDTO>>
     *
     * @author 이광석
     * @since  25.02.06
     */
    @PostMapping
    public ResponseEntity<GenericResponse<NotificationDTO>> createNotification(@RequestBody NotificationDTO notificationDTO
                                                                               ){
        NotificationDTO newNotificationDTO = notificationService.create(notificationDTO);
        return ResponseEntity.ok(GenericResponse.of(
                newNotificationDTO,
                "알람 생성 성공"
        ));

    }

    /**
     * 특정 유저 알람 조회
     * @param userDetails
     * @return ResponseEntity<GenericResponse<List<NotificationDTO>>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/myNotification")
    public ResponseEntity<GenericResponse<Page<NotificationDTO>>> getUserIdNotification(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                        @RequestParam(value = "page",defaultValue = "1") int page,
                                                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                        @RequestParam(value = "onlyNotCheck", defaultValue = "false") boolean onlyNotCheck){
        MemberDto memberDto = memberService.getMyProfile(userDetails.getUsername());
        Page<NotificationDTO> notificationDTOS = notificationService.findByUser(memberDto,page,size,onlyNotCheck);
        return ResponseEntity.ok(GenericResponse.of(
                notificationDTOS,
                "알림 조회 성공"
        ));
    }

    /**
     * 알림 읽음 상태 변경
     * @param notificationId
     * @return ResponseEntity<GenericResponse<String>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @PutMapping("/{notificationId}")
    public ResponseEntity<GenericResponse<String>> notificationCheck(@PathVariable("notificationId") Long notificationId,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails){

        MemberDto memberDto = memberService.getMyProfile(userDetails.getUsername());
        notificationService.notificationCheck(notificationId, memberDto);
        return ResponseEntity.ok(GenericResponse.of(
                "변경 성공"
        ));
    }

    /**
     * 알림 삭제
     * @param notificationId
     * @return ResponseEntity<GenericResponse<String>>
     *
     * @author 이광석
     * @since
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<GenericResponse<String>> notificationDelete(@PathVariable("notificationId") Long notificationId,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        MemberDto memberDto = memberService.getMyProfile(userDetails.getUsername());
        notificationService.deleteNotification(notificationId,memberDto);
        return ResponseEntity.ok(GenericResponse.of(
                "삭제 성공"
        ));
    }

    @GetMapping("/{notificationCount}")
    public ResponseEntity<GenericResponse<Long>> notificationTotalCount(@AuthenticationPrincipal CustomUserDetails userDetails){
        MemberDto memberDto = memberService.getMyProfile(userDetails.getUsername());
        Long total = notificationService.getNotificationTotalCount(memberDto);
        return ResponseEntity.ok(GenericResponse.of(
                total,
                "전체 알람 전달 성공"

        ));
    }
}
