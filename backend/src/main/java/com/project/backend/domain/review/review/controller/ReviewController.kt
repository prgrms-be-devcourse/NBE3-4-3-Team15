package com.project.backend.domain.review.review.controller

import com.project.backend.domain.member.service.MemberService
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO
import com.project.backend.domain.review.review.service.ReviewService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


/**
 * 리뷰 컨트롤러
 */
@Tag(name = "ReviewController", description = "리뷰 컨트롤러")
@RestController
@RequestMapping("/review")
@SecurityRequirement(name = "bearerAuth")
class ReviewController(
    private val  reviewService: ReviewService

) {


    /**
     * 리뷰 목록을 조회
     * @return -- ResponseEntity<GenericResponse></GenericResponse><List></List><ReviewsDTO>>> - 리뷰 목록
     *
     * @author -- 이광석
     * @since  -- 25.01.27
    </ReviewsDTO> */
    @GetMapping
    @Operation(summary = "리뷰 목록")
    fun getReviews(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<GenericResponse<Page<ReviewsDTO>>> {
        val pages = reviewService.findAll(page, size)
        return ResponseEntity.ok(
            GenericResponse.of(
                pages,
                "리뷰 목록 반환 성공"
            )
        )
    }

    /**
     * 특정 유저의 리뷰 목록 조회
     * @param userDetails
     * @return ResponseEntity<GenericResponse></GenericResponse><List></List><ReviewsDTO>>>
     *
     * @author 이광석
     * @since 25.02.06
    </ReviewsDTO> */
    @GetMapping("/myReview")
    @Operation(summary = "특정 유저의 리뷰 목록 조회")
    fun getUserReviews(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GenericResponse<List<ReviewsDTO>>> {
        val memberId = reviewService.myId(userDetails)
        val reviewsDTOS = reviewService.getUserReviews(memberId)
        return ResponseEntity.ok(
            GenericResponse.of(
                reviewsDTOS,
                "리뷰 목록 반환 성공"
            )
        )
    }

    /**
     * bookId 기반 리뷰(코멘트 포함)검색
     * @param bookId
     * @param page
     * @param size
     * @return ResponseEntity<GenericResponse></GenericResponse><List></List><ReviewsDTO>>>
     *
     * @author 이광석
     * @since 25.02.7
    </ReviewsDTO> */
    @GetMapping("/books/{bookId}")
    fun getBookIdReviews(
        @PathVariable("bookId") bookId: Long,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<GenericResponse<Page<ReviewsDTO>>> {
        val pages = reviewService.getBookIdReviews(bookId, page, size)

        return ResponseEntity.ok(
            GenericResponse.of(
                pages,
                "리뷰 조회 성공"
            )
        )
    }


    /**
     * 리뷰 추가
     *
     * @param -- ReviewsDTO(bookId,memberId,content,rating)
     * @return -- ResponseEntity<GenericResponse></GenericResponse><String>>
     *
     * @author -- 이광석
     * @since  -- 25.01.27
    </String> */
    @PostMapping
    @Operation(summary = "리뷰 추가")
    @Transactional
    fun postReview(
        @RequestBody reviewsDTO: ReviewsDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<String>> {
        val memberId = reviewService.myId(userDetails)
        reviewService.write(memberId, reviewsDTO)

        return ResponseEntity.ok(
            GenericResponse.of(
                "리뷰 추가 성공"
            )
        )
    }


    /**
     * 리뷰 수정
     * @param -- ReviewsDTO(content,rating)
     * @param -- id - 수정할 리뷰
     * @return -- ResponseEntity<GenericResponse></GenericResponse><ReviewsDTO>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
    </ReviewsDTO> */
    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정")
    @Transactional
    fun putReviews(
        @RequestBody reviewsDTO: ReviewsDTO,
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewsDTO>> {
        reviewService.authorityCheck(userDetails, reviewId)
        val memberId = reviewService.myId(userDetails)

        reviewService.modify(reviewsDTO, reviewId, memberId)
        return ResponseEntity.ok(
            GenericResponse.of(
                reviewsDTO,
                "리뷰 수정 성공"
            )
        )
    }


    /**
     * 리뷰 삭제
     * @param -- id - 삭제할 리뷰 id
     * @return -- ResponseEntity<GenericResponse></GenericResponse><ReviewsDTO>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
    </ReviewsDTO> */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제")
    @Transactional
    fun deleteReviews(
        @PathVariable("reviewId") id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewsDTO>> {
        reviewService.authorityCheck(userDetails, id)
        val memberId = reviewService.myId(userDetails)

        val review = reviewService.delete(id, memberId)
        return ResponseEntity.ok(
            GenericResponse.of(
                review,
                "리뷰 삭제 성공"
            )
        )
    }


    /**
     * 리뷰 추천/추천 취소
     * @param -- reviewId -- 리뷰 id
     * @param -- memberId -- 추천인 id
     * @return -- ResponseEntity<GenericResponse></GenericResponse><ReviewsDTO>>
     *
     * @author -- 이광석
     * @since -- 25.01.17
    </ReviewsDTO> */
    @PutMapping("/{reviewId}/recommend")
    @Operation(summary = "리뷰 추천")
    @Transactional
    fun recommendReview(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<ReviewsDTO>> {
        val memberId = reviewService.myId(userDetails)
        val result = reviewService.recommend(reviewId, memberId)

        val reviewsDTO = reviewService.getReview(reviewId)

        val message = if (result) "리뷰 추천 성공" else "리뷰 추천 취소 성공"
        return ResponseEntity.ok(
            GenericResponse.of(
                reviewsDTO,
                message
            )
        )
    }
}
