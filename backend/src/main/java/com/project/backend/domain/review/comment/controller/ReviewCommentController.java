package com.project.backend.domain.review.comment.controller;

import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.service.ReviewCommentService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰 댓글 컨트롤러
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Tag(name = "ReviewCommentController", description = "리뷰 댓글 컨트롤러")
@RestController
@RequestMapping("/review/{reviewId}/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;


    /**
     * 리뷰 코멘트 목록 조회
     * @param -- reviewId -- 리뷰 id
     * @return --  ResponseEntity<GenericResponse<List<ReviewCommentDto>>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @GetMapping
    @Operation(summary = "리뷰 댓글 목록 조회")
    public ResponseEntity<GenericResponse<List<ReviewCommentDto>>> getComments(@PathVariable("reviewId") Long reviewId){

        List<ReviewCommentDto> reviewCommentDtoList = reviewCommentService.findComment(reviewId);
        return ResponseEntity.ok( GenericResponse.of(
                reviewCommentDtoList
                ,"리뷰 코멘트 목록 조회 성공"
        ));
    }

    /**
     * 대댓글 조회
     * @param commentId
     * @return ResponseEntity<GenericResponse<List<ReviewCommentDto>>>
     *
     * @author 이광석
     * @since 25.02.05
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "대댓글 조회")
    public ResponseEntity<GenericResponse<List<ReviewCommentDto>>> getReplies(@PathVariable("commentId") Long commentId){
        List<ReviewCommentDto> replies = reviewCommentService.findReplies(commentId);
        return ResponseEntity.ok(GenericResponse.of(
                replies,
                "대댓글 목록 조회 성공"
        ));
    }

    /**
     * userId 기반 댓글 검색
     * @param userDetails
     * @return GenericResponse<List<ReviewCommentDto>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/review/comments")
    @Operation(summary = "댓글 검색")
    public ResponseEntity<GenericResponse<List<ReviewCommentDto>>> getUserComment(@AuthenticationPrincipal CustomUserDetails userDetails){
        long memberId = reviewCommentService.myId(userDetails);
        List<ReviewCommentDto> commentDtos = reviewCommentService.findUserComment(memberId);
        return ResponseEntity.ok(GenericResponse.of(

                commentDtos,
                "User 댓글 조회 성공"
        ));
    }



    /**
     *리뷰 코멘트 생성
     * @param reviewId
     * @param reviewCommentDto
     * @return ResponseEntity<GenericResponse<ReviewCommentDto>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PostMapping
    @Operation(summary = "리뷰 댓글 생성")

    public ResponseEntity<GenericResponse<ReviewCommentDto>> postComment(@PathVariable("reviewId") Long reviewId,
                                            @Valid @RequestBody ReviewCommentDto reviewCommentDto,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails){

        long memberId = reviewCommentService.myId(userDetails);
       ReviewCommentDto newReviewCommentDto = reviewCommentService.write(reviewId,reviewCommentDto,memberId);

       return ResponseEntity.ok(GenericResponse.of(
               newReviewCommentDto,
               "리뷰 코맨트 생성 성공"
       ));
    }

    /**
     * 리뷰 코멘트 메서드 수정
     * @param reviewId
     * @param commentId
     * @param reviewCommentDto
     * @return ResponseEntity<GenericResponse<ReviewCommentDto>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}")
    @Operation(summary = "리뷰 댓글 수정")
    @Transactional
    public ResponseEntity<GenericResponse<ReviewCommentDto>> putComment(@PathVariable("reviewId") Long reviewId,
                                                         @PathVariable("id") Long commentId,
                                                        @RequestBody ReviewCommentDto reviewCommentDto,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails){

        String username = userDetails.getUsername();
        reviewCommentService.authorityCheck(username,reviewCommentDto.getUserId());
            ReviewCommentDto newReviewCommentDto=reviewCommentService.modify(reviewId, commentId, reviewCommentDto);


            return ResponseEntity.ok(GenericResponse.of(
                    newReviewCommentDto,
                    "코멘트 수정 성공"
            ));
    }

    /**
     * 리뷰 코메트 삭제
     * @param reviewId
     * @param commentId
     * @return ResponseEntity<GenericResponse<ReviewCommentDto>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "리뷰 댓글 삭제")

    public ResponseEntity<GenericResponse<ReviewCommentDto>> delete(@PathVariable("reviewId") Long reviewId,
                                                    @PathVariable("id") Long commentId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails){

        ReviewCommentDto newReviewCommentDto = reviewCommentService.delete(reviewId,commentId);
        return ResponseEntity.ok(GenericResponse.of(

                newReviewCommentDto,
                "리뷰 코멘트 삭제 성공"
        ));
    }

    /**
     * 코멘트 추천
     * @param reviewId
     * @param commentId
     * @param userDetails
     * @return GenericResponse<ReviewCommentDto>(추천/추천 취소 메시지 다름)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}/recommend/{memberId}")
    @Operation(summary = "리뷰 댓글 추천")
    public ResponseEntity<GenericResponse<ReviewCommentDto>> recommendComment(@PathVariable("reviewId") Long reviewId,
                                                   @PathVariable("id") Long commentId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails){
       boolean result = reviewCommentService.recommend(commentId, userDetails.getUsername());
       ReviewCommentDto reviewCommentDto = reviewCommentService.findById(commentId);
       String message = result ? "리뷰 코멘트 추천 성공" : "리뷰 코멘트 추천 취소 성공";
        return ResponseEntity.ok(GenericResponse.of(reviewCommentDto, message));

    }


}
