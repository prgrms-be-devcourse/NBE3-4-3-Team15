package com.project.backend.domain.notification.controller;


import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    /**
     * 알림 생성
     * @param notificationDTO
     * @return GenericResponse<NotificationDTO>
     *
     * @author 이광석
     * @since  25.02.06
     */
    @PostMapping
    public GenericResponse<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO){
        NotificationDTO newNotificationDTO = notificationService.create(notificationDTO);
        return GenericResponse.of(
                newNotificationDTO,
                "알람 생성 성공"
        );

    }

    /**
     * 특정 유저 알람 조회
     * @param memberId
     * @return GenericResponse<List<NotificationDTO>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/{memberId}")
    public GenericResponse<List<NotificationDTO>> getUserIdNotification(@PathVariable("memberId") Long memberId ){
        List<NotificationDTO> notificationDTOS = notificationService.findByUser(memberId);
        return GenericResponse.of(
                notificationDTOS,
                "알림 조회 성공"
        );
    }

    /**
     * 알림 읽음 상태 변경
     * @param notificationId
     * @return GenericResponse<String>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @PutMapping("/{notificationId}")
    public GenericResponse<String> notificationCheck(@PathVariable("notificationId") Long notificationId ){
        notificationService.notificationCheck(notificationId);
        return GenericResponse.of(
                "변경 성공"
        );
    }

    /**
     * 알림 삭제
     * @param notificationId
     * @return GenericResponse<String>
     *
     * @author 이광석
     * @since
     */
    @DeleteMapping("/{notificationId}")
    public GenericResponse<String> notificationDelete(@PathVariable("notificationId") Long notificationId){
        notificationService.notificationDelete(notificationId);
        return GenericResponse.of(
                "삭제 성공"
        );
    }


//    @Operation(summary = "sse세션 연결")
//    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUserDetails userDetails,
//                                                @RequestHeader(value= "Last-Event-ID",required = false, defaultValue = "") String lastEventId){
////        return ResponseEntity.of(notificationService.subscribe(userDetails.getName(),lastEventId));
//    }
}
