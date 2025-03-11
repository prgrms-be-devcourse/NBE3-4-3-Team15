package com.project.backend.domain.review.comment.repository;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * 댓글 Repository
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findAllByReviewId(Integer reviewId);

    List<ReviewCommentDto> findByParent(ReviewComment parent);

    List<ReviewCommentDto> findAllByUserId(Long userId);

    @Query("SELECT rc.review.id, COUNT(rc.id) FROM ReviewComment rc WHERE rc.createdAt BETWEEN :start AND :end GROUP BY rc.review.id")
    List<Object[]> findReviewCommentCounts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
