package com.project.backend.domain.review.comment.controller;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.service.ReviewCommentService;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
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

    /**
     * userId 기반 댓글 검색
     * @param userId
     * @return GenericResponse<List<ReviewCommentDto>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/review/comments/{userId}")
    public GenericResponse<List<ReviewCommentDto>> getUserComment(@PathVariable("userId")Long userId){
        List<ReviewCommentDto> commentDtos = reviewCommentService.findUserComment(userId);
        return GenericResponse.of(
                commentDtos,
                "User 댓글 조회 성공"
        );
    }
}
