package com.project.backend.domain.review.review.controller;


import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
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
    public ResponseEntity<List<ReviewsDTO>> getReviews(){
        List<ReviewsDTO> reviewsDTOS = reviewService.findAll();
        return ResponseEntity.ok(reviewsDTOS);
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
    public ResponseEntity<String> postReview(@RequestBody ReviewsDTO reviewsDTO){
        reviewService.save(reviewsDTO);
        return ResponseEntity.ok("성공적으로 저장되었습니다.");
    }


    /**
     *
     * @param -- ReviewsDTO(content,rating)
     * @param -- id
     * @return -- 성공메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> putReviews(@RequestBody ReviewsDTO reviewsDTO,
                                             @PathVariable("id") Long id){
        reviewService.modify(reviewsDTO,id);
        return ResponseEntity.ok("성공적으로 수정하였습니다.");
    }


    /**
     *
     * @param -- id
     * @return -- 성공 메시지(상태코드 200)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReviews(@PathVariable("id") Long id){
        reviewService.delete(id);
        return ResponseEntity.ok("성공적으로 삭제하였습니다.");
    }


    /**
     *
     * @param -- reviewId -- 리뮤 id
     * @param -- memberId -- 추천인 id
     * @return -- 성공 메시지(상태코드 200);
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    @PutMapping("/{reviewId}/recommend/{memberId}")
    public ResponseEntity<String> recommendReview(@PathVariable("reviewId") Long reviewId,
                                                  @PathVariable("memberId") Long memberId){
        reviewService.recommend(reviewId,memberId);
        return ResponseEntity.ok("성공적으로 삭제하였습니다.");
    }
}
