package com.project.backend.domain.notification.service;


import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 알람 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    /**
     * 알람 생성
     * @param notificationDTO
     * @return NotificationDTO
     *
     * @author 이광석
     * @since 25.02.06
     */
    public NotificationDTO create(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .memberId(notificationDTO.getMemberId())
                .reviewId(notificationDTO.getReviewId())
                .reviewCommentId(notificationDTO.getReviewComment())
                .isCheck(notificationDTO.isCheck())
                .content(notificationDTO.getContent())
                .build();


        return new NotificationDTO(notificationRepository.save(notification));
    }

    /**
     * 알람 조회
     * @param memberId
     * @return List<NotificationDTO>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public List<NotificationDTO> findByUser(Long memberId) {
        return notificationRepository.findALLByMemberId(memberId);
    }

    /**
     * 알람 읽음 상태 변경
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    public void notificationCheck(Long notificationId) {
        Notification notification = findNotificationById(notificationId);
        notification.setCheck(true);
        notificationRepository.save(notification);

    }

    /**
     * 알람 삭제
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    public void notificationDelete(Long notificationId) {
        Notification notification = findNotificationById(notificationId);
        notificationRepository.delete(notification);
    }


    /**
     * Notification 탐색
     * @param notificationId
     * @return Notification
     *
     * @author 이광석
     * @since 25.02.09
     */
    private Notification findNotificationById(Long notificationId){
        return notificationRepository.findById(notificationId)
                .orElseThrow(()-> new RuntimeException("해당 알림을 찾을수 없습니다."));

    }
}
