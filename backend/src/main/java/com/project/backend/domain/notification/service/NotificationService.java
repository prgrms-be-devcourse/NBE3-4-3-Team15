package com.project.backend.domain.notification.service;


import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public NotificationDTO create(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .memberId(notificationDTO.getMemberId())
                .isCheck(notificationDTO.isCheck())
                .content(notificationDTO.getContent())
                .build();
        if(notificationDTO.getReviewId()!=null){
            notification.setReviewId(notificationDTO.getReviewId());
        }else if(notificationDTO.getReviewComment()!=null){
            notification.setReviewCommentId(notificationDTO.getReviewComment());
        }


        return new NotificationDTO(notificationRepository.save(notification));
    }

    public List<NotificationDTO> findByUser(Long memberId) {
        return notificationRepository.findALLByMemberId(memberId);
    }

    public void notificationCheck(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()->new RuntimeException("해당 알림 없음"));
        notification.setCheck(true);
        notificationRepository.save(notification);

    }
}
