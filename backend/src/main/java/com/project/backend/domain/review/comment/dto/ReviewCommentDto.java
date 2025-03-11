//package com.project.backend.domain.review.comment.dto;
//
//import com.project.backend.domain.member.dto.MemberDto;
//import com.project.backend.domain.member.entity.Member;
//import com.project.backend.domain.review.comment.entity.ReviewComment;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// *
// * 댓글 DTO
// *
// * @author shjung
// * @since 25. 1. 24.
// */
//@Getter
//@Setter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class ReviewCommentDto {
//
//    private Long id;
//
//
//
//    private Long reviewId;
//
//
//    private Long userId;
//
//    @NotBlank
//    private String comment;
//
//    private Long parentId;
//
//    private Integer depth;
//
//
//    private Set<MemberDto> recommend;
//
//
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime modifiedAt;
//    private List<ReviewCommentDto> replies;
//
//
//
//    public ReviewCommentDto(ReviewComment reviewComment) {
//        this.id = reviewComment.getId();
//        this.reviewId = reviewComment.getReview().getId();
//        this.userId = reviewComment.getUserId();
//        this.comment = reviewComment.getComment();
//        this.parentId = (reviewComment.getParent() == null)?null: reviewComment.getParent().getId();
//        this.depth = reviewComment.getDepth();
//        this.recommend = reviewComment.getRecommend().stream()
//                .map(MemberDto::new)
//                .collect(Collectors.toSet());
//        this.createdAt= reviewComment.getCreatedAt();
//        this.modifiedAt=reviewComment.getModifiedAt();
//        this.replies = (reviewComment.getReplies() != null)? reviewComment.getReplies().stream()
//                .map(ReviewCommentDto::new)
//                .collect(Collectors.toList()):null;
//
//    }
//}
