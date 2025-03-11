package com.project.backend.domain.review.comment.service

import com.project.backend.domain.member.entity.Member
import com.project.backend.domain.member.repository.MemberRepository
import com.project.backend.domain.member.service.MemberService
import com.project.backend.domain.notification.dto.NotificationDTO
import com.project.backend.domain.notification.entity.NotificationType
import com.project.backend.domain.notification.service.NotificationService
import com.project.backend.domain.review.comment.dto.ReviewCommentDto
import com.project.backend.domain.review.comment.entity.ReviewComment
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository
import com.project.backend.domain.review.exception.ReviewErrorCode
import com.project.backend.domain.review.exception.ReviewException
import com.project.backend.domain.review.review.entity.Review
import com.project.backend.domain.review.review.repository.ReviewRepository
import com.project.backend.domain.review.review.service.ReviewService
import com.project.backend.global.authority.CustomUserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReviewCommentService(
    private val reviewCommentRepository: ReviewCommentRepository,
    private val reviewService: ReviewService,
    private val reviewRepository: ReviewRepository,
    private val memberRepository: MemberRepository,
    private val notificationService: NotificationService,
    private val memberService: MemberService
) {

    fun findComment(reviewId: Long): List<ReviewCommentDto> {
        return emptyList()
    }

    fun findUserComment(memberId: Long): List<ReviewCommentDto> {
        return reviewCommentRepository.findAllByUserId(memberId).map { ReviewCommentDto.from(it) }
    }

    @Transactional
    fun write(reviewId: Long, reviewCommentDto: ReviewCommentDto, memberId: Long): ReviewCommentDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND) }

        val reviewComment = ReviewComment(
            review = review,
            userId = memberId,
            comment = reviewCommentDto.comment,
            recommend = mutableSetOf(),
            depth = 0,
            isDelete = false
        )

        reviewCommentDto.parentId?.let {
            val parentComment = findCommentById(it)

            if (parentComment.depth + 1 >= 2) {
                throw ReviewException(ReviewErrorCode.INVALID_COMMENT_DEPTH)
            }
            reviewComment.parent = parentComment
            reviewComment.depth = parentComment.depth + 1
        }

        val newReviewComment = reviewCommentRepository.save(reviewComment)
        createCommentNotification(newReviewComment, review, reviewCommentDto)

        return ReviewCommentDto.from(newReviewComment)
    }

    fun createCommentNotification(reviewComment: ReviewComment, review: Review, reviewCommentDto: ReviewCommentDto) {
        val producer = memberService.getMemberById(reviewComment.userId)
        val notificationType = if (reviewCommentDto.parentId == null) NotificationType.COMMENT else NotificationType.REVIEW

        val notificationDTO = NotificationDTO(
            consumerMemberId = review.userId,
            producerMemberId = reviewComment.userId,
            reviewCommentId = reviewComment.id,
            isCheck = false,
            notificationType = notificationType,
            content = notificationService.buildContent(producer.userNameK, notificationType),
            id = null,
            createdAt = LocalDateTime.now(),
            reviewId = null
        )
        notificationService.create(notificationDTO)
    }

    @Transactional
    fun modify(reviewId: Long, commentId: Long, reviewCommentDto: ReviewCommentDto): ReviewCommentDto {
        val reviewComment = findCommentById(commentId)
        reviewComment.comment = reviewCommentDto.comment
        return ReviewCommentDto.from(reviewCommentRepository.save(reviewComment))
    }

    @Transactional
    fun delete(reviewId: Long, commentId: Long): ReviewCommentDto {
        val reviewComment = findCommentById(commentId)

        if (reviewComment.parent != null) {
            deleteReply(reviewComment, reviewId)
        } else {
            deleteComment(reviewComment, reviewId)
        }

        return ReviewCommentDto.from(reviewComment)
    }

    @Transactional
    fun recommend(commentId: Long, username: String): Boolean {
        val reviewComment = findCommentById(commentId)
        val member = memberRepository.findByUsername(username)
            .orElseThrow { ReviewException(ReviewErrorCode.MEMBER_NOT_FOUND) }

        return if (reviewComment.recommend.contains(member)) {
            reviewComment.recommend.remove(member)
            reviewCommentRepository.save(reviewComment)
            false
        } else {
            reviewComment.recommend.add(member)
            reviewCommentRepository.save(reviewComment)
            true
        }
    }

    fun findById(commentId: Long): ReviewCommentDto {
        return ReviewCommentDto.from(findCommentById(commentId))
    }

    fun findReplies(commentId: Long): List<ReviewCommentDto> {
        val parent = findCommentById(commentId)
        return reviewCommentRepository.findByParent(parent).map { ReviewCommentDto.from(it) }
    }

    private fun findCommentById(commentId: Long): ReviewComment {
        return reviewCommentRepository.findById(commentId)
            .orElseThrow { ReviewException(ReviewErrorCode.COMMENT_NOT_FOUND) }
    }

    fun myId(userDetails: CustomUserDetails): Long {
        return memberService.getMyProfile(userDetails.username).id
    }

    fun authorityCheck(username: String, commentUserId: Long) {
        val member = memberRepository.findById(commentUserId)
            .orElseThrow { ReviewException(ReviewErrorCode.MEMBER_NOT_FOUND) }

        if (member.username != username) {
            throw ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS)
        }
    }

    @Transactional
    fun deleteComment(comment: ReviewComment, reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND) }

        review.comments.remove(comment)

        if (comment.replies.isEmpty()) {
            reviewCommentRepository.delete(comment)
            if (review.comments.isEmpty()) {
                reviewService.reviewDelete(review)
            }
        } else {
            comment.isDelete = true
            comment.comment = "해당 댓글은 삭제되었습니다"
            reviewCommentRepository.save(comment)
        }
    }

    @Transactional
    fun deleteReply(reply: ReviewComment, reviewId: Long) {
        val parent = reply.parent ?: return
        parent.replies.remove(reply)
        reviewCommentRepository.delete(reply)

        if (parent.isDelete && parent.replies.isEmpty()) {
            deleteComment(parent, reviewId)
        }
    }
}
