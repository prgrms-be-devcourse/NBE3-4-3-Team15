package com.project.backend.domain.notification.controller;


import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping
    public GenericResponse<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO){
        NotificationDTO newNotificationDTO = notificationService.create(notificationDTO);
        return GenericResponse.of(
                newNotificationDTO,
                "알람 생성 성공"
        );

    }
}
