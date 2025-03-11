package com.project.backend.domain.review.review.reviewDTO

import com.project.backend.domain.member.dto.MemberDto
import com.project.backend.domain.review.comment.dto.ReviewCommentDto
import com.project.backend.domain.review.review.entity.Review
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import net.bytebuddy.asm.Advice.Local
import java.time.LocalDateTime
import java.util.stream.Collectors

/**
 * 리뷰DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
data class ReviewsDTO(
    val id:  Long?,
    val bookId: Long,
    val userId: Long,
    val content: String,
    val rating: Int,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?

){
    companion object{
        fun from(review:Review)=ReviewsDTO(
            id = review.id,
            bookId = review.bookId,
            userId=review.userId,
            content=review.content,
            rating=review.rating ,
            createdAt = review.createdAt,
            modifiedAt = review.modifiedAt
        )
    }

}
