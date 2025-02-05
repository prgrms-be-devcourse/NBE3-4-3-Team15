package com.project.backend.domain.review.review.controller;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
import com.project.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")

public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 목록을 반환
     * @return -- GenericResponse<List<ReviewsDTO>> - 리뷰 목록
     *
     * @author -- 이광석
     * @since  -- 25.01.27
     */
    @GetMapping
    public GenericResponse<List<ReviewsDTO>> getReviews(){
        List<ReviewsDTO> reviewsDTOS = reviewService.findAll();
        return GenericResponse.of(
                reviewsDTOS,
                "리뷰 목록 반환 성공"
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
    public GenericResponse<String> postReview(@Valid @RequestBody ReviewsDTO reviewsDTO){

        reviewService.write(reviewsDTO);


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
    @PutMapping("/{id}")
    public GenericResponse<ReviewsDTO> putReviews( @RequestBody ReviewsDTO reviewsDTO,
                                             @PathVariable("id") Long id){
        reviewService.modify(reviewsDTO,id);
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
    @DeleteMapping("/{id}")
    public GenericResponse<ReviewsDTO> deleteReviews(@PathVariable("id") Long id){
        ReviewsDTO review=  reviewService.delete(id);

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
    @PutMapping("/{reviewId}/recommend/{memberId}")
    public GenericResponse<ReviewsDTO> recommendReview(@PathVariable("reviewId") Long reviewId,
                                                  @PathVariable("memberId") Long memberId){
        boolean result = reviewService.recommend(reviewId,memberId);
        ReviewsDTO reviewsDTO = reviewService.findById(reviewId);



        String message = result ?"리뷰 추천 성공" : "리뷰 추천 취소 성공";
        return GenericResponse.of(
                reviewsDTO,
                message
        );
    }





}
