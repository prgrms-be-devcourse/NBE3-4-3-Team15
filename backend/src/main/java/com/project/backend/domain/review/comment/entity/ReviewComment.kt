package com.project.backend.domain.review.comment.entity

import com.project.backend.domain.member.entity.Member
import com.project.backend.domain.review.review.entity.Review
import com.project.backend.global.baseEntity.BaseEntityK
import jakarta.persistence.*

/**
 * 댓글 Entity
 */
@Entity
class ReviewComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    val review: Review,

    val userId: Long,

    var comment: String,

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: ReviewComment? = null,

    var depth: Int = 0,

    @ManyToMany
    var recommend: MutableSet<Member> = mutableSetOf(),

    var isDelete: Boolean = false,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var replies: MutableList<ReviewComment> = mutableListOf()  // 자식 댓글 (대댓글)
) : BaseEntityK()
