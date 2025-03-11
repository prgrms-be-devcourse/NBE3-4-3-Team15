package com.project.backend.domain.review.review.entity

import com.project.backend.domain.member.entity.Member
import com.project.backend.domain.review.comment.entity.ReviewComment
import com.project.backend.domain.review.recommendation.entity.ReviewRecommendation
import com.project.backend.global.baseEntity.BaseEntity
import com.project.backend.global.baseEntity.BaseEntityK
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.*


/**
 * 리뷰
 *
 * @author 이광석
 * @since 25.02.04
 */
@Entity

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class Review (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull
    val bookId: Long,

    @NotNull
    val userId:  Long,

    @NotBlank
    var content: String,

    @Min(0)
    @Max(10)
    @NotNull
    var rating:  Int,

    @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<ReviewComment> = mutableListOf(),

    @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL], orphanRemoval = true)
    var recommendations: MutableList<ReviewRecommendation> = mutableListOf(),

    var isDelete: Boolean = false
) :BaseEntityK()
