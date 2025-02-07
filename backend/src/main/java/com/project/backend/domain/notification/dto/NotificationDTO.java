package com.project.backend.domain.notification.dto;


import com.project.backend.domain.notification.entity.Notification;
import lombok.*;




@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    public void setContent(String username, String type){
        String content= username + "님이 ";
        if(type.equals("COMMENT")) {
            content += "댓글을 작성하였습니다";
        }else if(type.equals("REPLY")){
            content+= "대댓글을 작성하였습니다";
        }
        this.content = content;
    }
}
