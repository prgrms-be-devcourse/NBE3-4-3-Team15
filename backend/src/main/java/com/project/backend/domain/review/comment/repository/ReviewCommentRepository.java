package com.project.backend.domain.review.comment.repository;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
