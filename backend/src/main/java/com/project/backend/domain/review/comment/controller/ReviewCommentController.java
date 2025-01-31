package com.project.backend.domain.review.comment.controller;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.List;

/**
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


    /**
     * 리뷰 코멘트 목록 조회
     * @param -- reviewId -- 리뷰 id
     * @return -- reviewCommentDtoList
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @GetMapping
    public ResponseEntity<List<ReviewCommentDto>> getComments(@PathVariable("reviewId") Integer reviewId){
        try {
            List<ReviewCommentDto> reviewCommentDtoList = reviewCommentService.findByReview(reviewId);
            return ResponseEntity.ok(reviewCommentDtoList);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }

    /**
     *리뷰 코멘트 생성
     * @param reviewId
     * @param reviewCommentDto
     * @return 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PostMapping
    public ResponseEntity<String> postComment(@PathVariable("reviewId") Integer reviewId,
                                              @RequestBody ReviewCommentDto reviewCommentDto){

        try {
            reviewCommentService.write(reviewId, reviewCommentDto);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("댓글 작성에 실패 했습니다");
        }
        return ResponseEntity.ok("성공적으로 댓글을 작성했습니다.");
    }

    /**
     * 리뷰 코멘트 메서드 수정
     * @param reviewId
     * @param commentId
     * @param reviewCommentDto
     * @return 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> putComment(@PathVariable("reviewId") Long reviewId,
                                             @PathVariable("id") Integer commentId,
                                             @RequestBody ReviewCommentDto reviewCommentDto){
        try {
            reviewCommentService.modify(reviewId, commentId, reviewCommentDto);
            return ResponseEntity.ok("성공적으로 댓글을 수정했습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("댓글 수정을 실패했습니다.");
        }
    }

    /**
     * 리뷰 코메트 삭제
     * @param reviewId
     * @param commentId
     * @return 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("reviewId") Long reviewId,
                                         @PathVariable("id") Integer commentId){
        try {
            reviewCommentService.delete(commentId);
            return ResponseEntity.ok("성공적으로 댓글을 삭제했습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("댓글 삭제를 실패했습니다");
        }

    }

    /**
     * 코멘트 추천
     * @param reviewId
     * @param commentId
     * @param memberId
     * @return 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}/recommend/{memberId}")
    public ResponseEntity<String> recommendComment(@PathVariable("reviewId") Long reviewId,
                                                   @PathVariable("id") Integer commentId,
                                                   @PathVariable("memberId") String memberId){
        System.out.println(memberId);
        try {
            reviewCommentService.recommend(commentId, memberId);
        }catch (RuntimeException re){
            return ResponseEntity.badRequest().body("뭔가 잘못된");
        }
       return ResponseEntity.ok("성공적으로 작업을 완료했습니다.");
    }
}
