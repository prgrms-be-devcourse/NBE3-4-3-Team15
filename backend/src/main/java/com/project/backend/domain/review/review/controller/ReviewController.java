package com.project.backend.domain.review.review.controller;


import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰 컨트롤러
 */
@Tag(name = "ReviewController", description = "리뷰 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {
    private final ReviewService reviewService;


    /**
     * 리뷰 목록을 조회
     * @return -- GenericResponse<List<ReviewsDTO>> - 리뷰 목록
     *
     * @author -- 이광석
     * @since  -- 25.01.27
     */
    @GetMapping
    @Operation(summary = "리뷰 목록")
    public GenericResponse<List<ReviewsDTO>> getReviews(@RequestParam(value="page",defaultValue = "0")int page,
                                                        @RequestParam(value="size",defaultValue="10")int size){
        List<ReviewsDTO> reviewsDTOS = reviewService.findAll(page,size);
        return GenericResponse.of(
                reviewsDTOS,
                "리뷰 목록 반환 성공"
        );
    }

    /**
     * 특정 유저의 리뷰 목록 조회
     * @param userDetails
     * @return GenericResponse<List<ReviewsDTO>>
     *
     * @author 이광석
     * @since 25.02.06
     */
    @GetMapping("/myReview")
    @Operation(summary = "특정 유저의 리뷰 목록 조회")
    public GenericResponse<List<ReviewsDTO>> getUserReviews(@AuthenticationPrincipal CustomUserDetails userDetails){

        List<ReviewsDTO> reviewsDTOS = reviewService.getUserReviews(userDetails);
        return GenericResponse.of(
                reviewsDTOS,
                "리뷰 목록 반환 성공"
        );
    }


    @GetMapping("/books/{bookId}")
    public GenericResponse<List<ReviewsDTO>> getBookIdReviews(@PathVariable("bookId") Long bookId,
                                                              @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size",defaultValue = "10") Integer size){
        List<ReviewsDTO> reviewsDTOS = reviewService.getBookIdReviews(bookId,page,size);

        return GenericResponse.of(
                reviewsDTOS,
                "리뷰 조회 성공"
        );
    }



    /**
     * 리뷰 추가
     *
     * @param -- ReviewsDTO(bookId,memberId,content,rating)
     * @return -- GenericResponse<ReviewsDTO>
     *
     * @author -- 이광석
     * @since  -- 25.01.27
     */
    @PostMapping
    @Operation(summary = "리뷰 추가")
    @Transactional
    public GenericResponse<String> postReview( @RequestBody ReviewsDTO reviewsDTO,
                                               @AuthenticationPrincipal CustomUserDetails userDetails){
        reviewService.write(userDetails,reviewsDTO);


        return GenericResponse.of(
                "리뷰 추가 성공"
        );
    }


    /**
     *리뷰 수정
     * @param -- ReviewsDTO(content,rating)
     * @param -- id - 수정할 리뷰
     * @return -- GenericResponse<ReviewsDTO>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정")
    @Transactional
    public GenericResponse<ReviewsDTO> putReviews( @RequestBody ReviewsDTO reviewsDTO,
                                             @PathVariable("reviewId") Long reviewId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        reviewService.modify(reviewsDTO,reviewId,userDetails);
        return GenericResponse.of(
                reviewsDTO,
                "리뷰 수정 성공"
        );
    }


    /**
     *리뷰 삭제
     * @param -- id - 삭제할 리뷰 id
     * @return -- GenericResponse<ReviewsDTO>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제")
    @Transactional
    public GenericResponse<ReviewsDTO> deleteReviews(@PathVariable("reviewId") Long id,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        ReviewsDTO review=  reviewService.delete(id,userDetails);

        return GenericResponse.of(
                review,
                "리뷰 삭제 성공"
        );
    }


    /**
     *리뷰 추천/추천 취소
     * @param -- reviewId -- 리뷰 id
     * @param -- memberId -- 추천인 id
     * @return -- GenericResponse<ReviewsDTO>
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{reviewId}/recommend")
    @Operation(summary = "리뷰 추천")
    @Transactional
    public GenericResponse<ReviewsDTO> recommendReview(@PathVariable("reviewId") Long reviewId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails){

        boolean result = reviewService.recommend(reviewId,userDetails);
        ReviewsDTO reviewsDTO = reviewService.getReview(reviewId);

        String message = result ?"리뷰 추천 성공" : "리뷰 추천 취소 성공";
        return GenericResponse.of(
                reviewsDTO,
                message
        );
    }


}
