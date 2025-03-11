package com.project.backend.domain.review.comment.controller

import com.project.backend.domain.review.comment.dto.ReviewCommentDto
import com.project.backend.domain.review.comment.service.ReviewCommentService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "ReviewCommentController", description = "리뷰 댓글 컨트롤러")
@RestController
@RequestMapping("/review/{reviewId}/comments")
@SecurityRequirement(name = "bearerAuth")
class ReviewCommentController(
    private val reviewCommentService: ReviewCommentService
) {

    /**
     * 리뷰 코멘트 목록 조회
     */
    @GetMapping
    @Operation(summary = "리뷰 댓글 목록 조회")
    fun getComments(@PathVariable reviewId: Long): ResponseEntity<GenericResponse<List<ReviewCommentDto>>> {
        val reviewCommentDtoList = reviewCommentService.findComment(reviewId)
        return ResponseEntity.ok(GenericResponse.of(reviewCommentDtoList, "리뷰 코멘트 목록 조회 성공"))
    }

    /**
     * 대댓글 조회
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "대댓글 조회")
    fun getReplies(@PathVariable commentId: Long): ResponseEntity<GenericResponse<List<ReviewCommentDto>>> {
        val replies = reviewCommentService.findReplies(commentId)
        return ResponseEntity.ok(GenericResponse.of(replies, "대댓글 목록 조회 성공"))
    }

    /**
     * userId 기반 댓글 검색
     */
    @GetMapping("/review/comments")
    @Operation(summary = "댓글 검색")
    fun getUserComment(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GenericResponse<List<ReviewCommentDto>>> {
        val memberId = reviewCommentService.myId(userDetails)
        val commentDtos = reviewCommentService.findUserComment(memberId)
        return ResponseEntity.ok(GenericResponse.of(commentDtos, "User 댓글 조회 성공"))
    }

    /**
     * 리뷰 코멘트 생성
     */
    @PostMapping
    @Operation(summary = "리뷰 댓글 생성")
    fun postComment(
        @PathVariable reviewId: Long,
        @Valid @RequestBody reviewCommentDto: ReviewCommentDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewCommentDto>> {
        val memberId = reviewCommentService.myId(userDetails)
        val newReviewCommentDto = reviewCommentService.write(reviewId, reviewCommentDto, memberId)

        return ResponseEntity.ok(GenericResponse.of(newReviewCommentDto, "리뷰 코멘트 생성 성공"))
    }

    /**
     * 리뷰 코멘트 수정
     */
    @PutMapping("/{commentId}")
    @Operation(summary = "리뷰 댓글 수정")
    @Transactional
    fun putComment(
        @PathVariable reviewId: Long,
        @PathVariable commentId: Long,
        @RequestBody reviewCommentDto: ReviewCommentDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewCommentDto>> {
        reviewCommentService.authorityCheck(userDetails.username, reviewCommentDto.userId)
        val newReviewCommentDto = reviewCommentService.modify(reviewId, commentId, reviewCommentDto)

        return ResponseEntity.ok(GenericResponse.of(newReviewCommentDto, "코멘트 수정 성공"))
    }

    /**
     * 리뷰 코멘트 삭제
     */
    @DeleteMapping("/{commentId}")
    @Transactional
    @Operation(summary = "리뷰 댓글 삭제")
    fun delete(
        @PathVariable reviewId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewCommentDto>> {
        val newReviewCommentDto = reviewCommentService.delete(reviewId, commentId)
        return ResponseEntity.ok(GenericResponse.of(newReviewCommentDto, "리뷰 코멘트 삭제 성공"))
    }

    /**
     * 코멘트 추천/추천 취소
     */
    @PutMapping("/{commentId}/recommend")
    @Operation(summary = "리뷰 댓글 추천")
    fun recommendComment(
        @PathVariable reviewId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewCommentDto>> {
        val result = reviewCommentService.recommend(commentId, userDetails.username)
        val reviewCommentDto = reviewCommentService.findById(commentId)
        val message = if (result) "리뷰 코멘트 추천 성공" else "리뷰 코멘트 추천 취소 성공"
        return ResponseEntity.ok(GenericResponse.of(reviewCommentDto, message))
    }
}
