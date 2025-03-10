package com.project.backend.domain.review.comment.controller;

import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.service.ReviewCommentService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * userId 관련 코멘트 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class ReviewCommentUserController {
    private final ReviewCommentService reviewCommentService;
    private final MemberService memberService;

    /**
     * userId 기반 댓글 검색
     * @param userDetails
     * @return GenericResponse<List<ReviewCommentDto>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/review/comments")
    public  ResponseEntity<GenericResponse<List<ReviewCommentDto>>> getUserComment(@AuthenticationPrincipal CustomUserDetails userDetails){
        long memberId = reviewCommentService.myId(userDetails);
        List<ReviewCommentDto> commentDtos = reviewCommentService.findUserComment(memberId);
        return ResponseEntity.ok(GenericResponse.of(

                commentDtos,
                "User 댓글 조회 성공"
        ));
    }
}
