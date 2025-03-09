package com.project.backend.domain.notification.dto;


import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.entity.NotificationType;
import com.project.backend.domain.notification.service.NotificationService;
import lombok.*;




@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    Long id;

    Long producerMemberId;

    Long consumerMemberId;

    Long reviewId;

    Long reviewCommentId;

    boolean isCheck;

    String content;

    NotificationType notificationType;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.producerMemberId = notification.getProducerMemberId();
        this.consumerMemberId = notification.getConsumerMemberId();
        this.reviewId = notification.getReviewId();
        this.reviewCommentId = notification.getReviewCommentId();
        this.isCheck = notification.isCheck();
        this.content = notification.getContent();
        this.notificationType = notification.getNotificationType();
    }


}
