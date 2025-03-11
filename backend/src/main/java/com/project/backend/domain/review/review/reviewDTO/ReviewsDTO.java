package com.project.backend.domain.review.review.reviewDTO;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.recommendation.dto.ReviewRecommendationDto;
import com.project.backend.domain.review.review.entity.Review;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 리뷰DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsDTO {
    Long id;

    @NotNull
    Long bookId;

    Long userId;

    @NotBlank
    String content;

    @NotNull
    @Min(0)
    @Max(10)
    Integer rating;

    List<ReviewCommentDto>  reviewCommentDtos;

    List<ReviewRecommendationDto> reviewRecommendationDtos;

    LocalDateTime createdAt;

    LocalDateTime modifiedAt;


    public ReviewsDTO(Review review){
        this.id=review.getId();
        this.bookId = review.getBookId();
        this.userId= review.getUserId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.reviewCommentDtos = review.getComments().stream()
                .filter(comment->comment.getParent()==null)
                .map(ReviewCommentDto::new)
                .collect(Collectors.toList());
        this.reviewRecommendationDtos = review.getRecommendations().stream() // 변경된 부분
                .map(ReviewRecommendationDto::new)
                .collect(Collectors.toList());
        this.createdAt=review.getCreatedAt();
        this.modifiedAt = review.getModifiedAt();
    }
}
