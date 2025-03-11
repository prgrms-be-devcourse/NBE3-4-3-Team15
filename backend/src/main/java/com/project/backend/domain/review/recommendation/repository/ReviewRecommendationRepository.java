package com.project.backend.domain.review.recommendation.repository;

import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.recommendation.entity.ReviewRecommendation;
import com.project.backend.domain.review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRecommendationRepository extends JpaRepository<ReviewRecommendation, Long> {
    Optional<ReviewRecommendation> findByReviewAndMember(Review review, Member member);

    @Query("SELECT rr.review.id, COUNT(rr.id) FROM ReviewRecommendation rr WHERE rr.recommendAt BETWEEN :start AND :end GROUP BY rr.review.id")
    List<Object[]> findReviewRecommendCounts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
