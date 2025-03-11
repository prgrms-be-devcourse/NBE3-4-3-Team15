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

    /**
     * 특정 기간 동안 받은 리뷰 추천 수를 리뷰별로 집계하여 조회하는 메서드
     *
     * @param start 조회 시작 날짜
     * @param end 조회 종료 날짜
     * @return 각 리뷰의 ID와 해당 기간 동안 받은 추천 개수를 포함한 리스트
     *
     * @author 김남우
     * @since 2025.03.10
     */
    @Query("SELECT rr.review.id, COUNT(rr.id) FROM ReviewRecommendation rr WHERE rr.recommendAt BETWEEN :start AND :end GROUP BY rr.review.id")
    List<Object[]> findReviewRecommendCounts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
