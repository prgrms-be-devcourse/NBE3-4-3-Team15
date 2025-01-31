package com.project.backend.domain.review.review.controller;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
import com.project.backend.global.response.GenericResponse;
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
     * @return -- ResponseEntity<List<ReviewsDTO>> - 리뷰 목록
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
     * @return -- 성공메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since  -- 25.01.27
     */
    @PostMapping
    public GenericResponse<ReviewsDTO> postReview(@RequestBody ReviewsDTO reviewsDTO){
//        try {
//            reviewService.write(reviewsDTO);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body("잘못된 요청입니다");
//        }
//        return ResponseEntity.ok("성공적으로 저장되었습니다.");
        reviewService.write(reviewsDTO);
        return GenericResponse.of(
                reviewsDTO,
                "리뷰 추가 성공"
        );
    }


    /**
     *리뷰 수정
     * @param -- ReviewsDTO(content,rating)
     * @param -- id
     * @return -- 성공메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}")
    public GenericResponse<ReviewsDTO> putReviews(@RequestBody ReviewsDTO reviewsDTO,
                                             @PathVariable("id") Integer id){
        reviewService.modify(reviewsDTO,id);
        return GenericResponse.of(
                reviewsDTO,
                "리뷰 수정 성공"
        );
    }


    /**
     *리뷰 삭제
     * @param -- id
     * @return -- 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @DeleteMapping("/{id}")
    public GenericResponse<ReviewsDTO> deleteReviews(@PathVariable("id") Integer id){
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
     * @return -- 성공 메시지(상태코드 200);
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{reviewId}/recommend/{memberId}")
    public GenericResponse<ReviewsDTO> recommendReview(@PathVariable("reviewId") Integer reviewId,
                                                  @PathVariable("memberId") String memberId){
        boolean result = reviewService.recommend(reviewId,memberId);
        ReviewsDTO reviewsDTO = reviewService.findById(reviewId);

        if(result) {
          return  GenericResponse.of(
                    reviewsDTO,
                    "리뷰 추천 성공"
            );
        }else{
            return GenericResponse.of(
                    reviewsDTO,
                    "리뷰 추천 취소 성공"
            );
        }
    }



}
