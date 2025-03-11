package com.project.backend.domain.review.recommendation.entity;

import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 리뷰 추천 엔티티
 *
 * @author 김남우
 * @since 2025.03.10
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime recommendAt;

    public static ReviewRecommendation of(Review review, Member member) {
        return new ReviewRecommendation(null, review, member, LocalDateTime.now());
    }
}
