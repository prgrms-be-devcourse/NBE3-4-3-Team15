package com.project.backend.domain.notification.controller;


import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
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
}
