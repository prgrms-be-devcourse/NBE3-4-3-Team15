package com.project.backend.domain.review.comment.repository

import com.project.backend.domain.review.comment.entity.ReviewComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 댓글 Repository
 */
@Repository
interface ReviewCommentRepository : JpaRepository<ReviewComment, Long> {
    fun findAllByReviewId(reviewId: Long): List<ReviewComment>

    fun findByParent(parent: ReviewComment): List<ReviewComment>

    fun findAllByUserId(userId: Long): List<ReviewComment>
}
