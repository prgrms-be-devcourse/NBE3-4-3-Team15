package com.project.backend.domain.review.recommendation.repository;

import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.recommendation.entity.ReviewRecommendation;
import com.project.backend.domain.review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRecommendationRepository extends JpaRepository<ReviewRecommendation, Long> {
    Optional<ReviewRecommendation> findByReviewAndMember(Review review, Member member);
}
