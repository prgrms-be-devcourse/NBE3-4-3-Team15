package com.project.backend.domain.review.comment.repository;

import com.project.backend.domain.review.comment.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * 댓글 Repository
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Integer> {
}
