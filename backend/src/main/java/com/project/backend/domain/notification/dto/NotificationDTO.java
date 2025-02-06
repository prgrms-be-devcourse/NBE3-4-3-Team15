package com.project.backend.domain.notification.dto;


import com.project.backend.domain.notification.entity.Notification;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    Long id;

    Long memberId;

    Long reviewId;

    Long reviewComment;

    boolean isCheck;

    String content;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.memberId = notification.getMemberId();
        this.reviewId = notification.getReviewId();
        this.reviewComment = notification.getReviewCommentId();
        this.isCheck = notification.isCheck();
        this.content = notification.getContent();
    }
}
