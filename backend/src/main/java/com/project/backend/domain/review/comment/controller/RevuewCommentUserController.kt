package com.project.backend.domain.review.comment.controller

import com.project.backend.domain.member.service.MemberService
import com.project.backend.domain.review.comment.dto.ReviewCommentDto
import com.project.backend.domain.review.comment.service.ReviewCommentService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * userId 관련 코멘트 컨트롤러
 */
@RestController
class ReviewCommentUserController(
    private val reviewCommentService: ReviewCommentService,
    private val memberService: MemberService
) {

    /**
     * userId 기반 댓글 검색
     */
    @GetMapping("/review/comments")
    fun getUserComment(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GenericResponse<List<ReviewCommentDto>>> {
        val memberId = reviewCommentService.myId(userDetails)
        val commentDtos = reviewCommentService.findUserComment(memberId)
        return ResponseEntity.ok(GenericResponse.of(commentDtos, "User 댓글 조회 성공"))
    }
}
