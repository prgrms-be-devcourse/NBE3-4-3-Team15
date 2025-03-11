package com.project.backend.domain.review.comment.dto

import com.project.backend.domain.member.dto.MemberDto
import com.project.backend.domain.review.comment.entity.ReviewComment
import java.time.LocalDateTime

/**
 * 댓글 DTO
 */
data class ReviewCommentDto(
    val id: Long?,
    val reviewId: Long?,
    val userId: Long,
    val comment: String,
    val parentId: Long?,
    val depth: Int?,
    val recommend: Set<MemberDto> = emptySet(),
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
    val replies: List<ReviewCommentDto> = emptyList()
) {
    companion object {
        fun from(reviewComment: ReviewComment): ReviewCommentDto {
            return ReviewCommentDto(
                id = reviewComment.id,
                reviewId = reviewComment.review.id,
                userId = reviewComment.userId,
                comment = reviewComment.comment,
                parentId = reviewComment.parent?.id,
                depth = reviewComment.depth,
                recommend = reviewComment.recommend.map { MemberDto(it) }.toSet(),
                createdAt = reviewComment.createdAt,
                modifiedAt = reviewComment.modifiedAt,
                replies = reviewComment.replies?.map { from(it) } ?: emptyList()
            )
        }
    }
}
