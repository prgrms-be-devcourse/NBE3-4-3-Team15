package com.project.backend.domain.review.recommendation.dto;

import com.project.backend.domain.review.recommendation.entity.ReviewRecommendation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 추천 정보를 담는 DTO
 *
 * @author 김남우
 * @since 2025.03.10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRecommendationDto {
    private Long id;
    private Long reviewId;
    private Long memberId;
    private LocalDateTime recommendAt;

    public ReviewRecommendationDto(ReviewRecommendation recommendation) {
        this.id = recommendation.getId();
        this.reviewId = recommendation.getReview().getId();
        this.memberId = recommendation.getMember().getId();
        this.recommendAt = recommendation.getRecommendAt();
    }
}
