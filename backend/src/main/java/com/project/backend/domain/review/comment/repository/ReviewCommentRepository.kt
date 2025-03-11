package com.project.backend.domain.review.comment.repository

import com.project.backend.domain.review.comment.entity.ReviewComment
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 댓글 Repository
 */
@Repository
interface ReviewCommentRepository : JpaRepository<ReviewComment, Long> {
    fun findAllByReviewId(reviewId: Long): List<ReviewComment>

    fun findByParent(parent: ReviewComment): List<ReviewComment>

    fun findAllByUserId(userId: Long): List<ReviewComment>

    /**
     * 특정 기간 동안 작성된 리뷰 댓글 수를 리뷰별로 집계하여 조회하는 메서드
     *
     * @param start 조회 시작 날짜
     * @param end 조회 종료 날짜
     * @return 각 리뷰의 ID와 해당 기간 동안 작성된 댓글 개수를 포함한 리스트
     *
     * @author 김남우
     * @since 2025.03.10
     */
    @Query("SELECT rc.review.id, COUNT(rc.id) FROM ReviewComment rc WHERE rc.createdAt BETWEEN :start AND :end GROUP BY rc.review.id")
    fun findReviewCommentCounts(
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime
    ): List<Array<Any>>

}
