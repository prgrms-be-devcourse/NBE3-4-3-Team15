package com.project.backend.domain.review.comment.controller;

import com.project.backend.domain.review.comment.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 리뷰 댓글 컨트롤러
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@RestController
@RequestMapping("/review/{reviewId}/comments")
@RequiredArgsConstructor
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;
}
