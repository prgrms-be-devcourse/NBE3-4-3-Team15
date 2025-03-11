//package com.project.backend.domain.notification.entity;
//
//
//import com.project.backend.domain.member.entity.Member;
//import com.project.backend.domain.review.comment.entity.ReviewComment;
//import com.project.backend.domain.review.review.entity.Review;
//import com.project.backend.global.baseEntity.BaseEntity;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//@Entity
//@Builder
//@Setter
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Notification extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    Long producerMemberId;
//
//    Long consumerMemberId;
//
//    Long reviewId;
//
//    Long reviewCommentId;
//
//
//    boolean isCheck;
//
//    String content;
//
//
//    @Enumerated(EnumType.STRING)
//    private NotificationType notificationType;
//}
