package com.project.backend.domain.review.review.service;


import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.exception.ReviewErrorCode;
import com.project.backend.domain.review.exception.ReviewException;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 리뷰 서비스
 *
 * @author 이광석
 * @since 25.01.27
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뮤 전체 조회
     *
     * @return List<ReviewsDTO>
     *
     * @author 이광석
     * @since 25.01.27
     */
    public List<ReviewsDTO> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewsDTO> reviewsDTOS = reviews.stream()
                .map(ReviewsDTO::new)
                .collect(Collectors.toList());
        return reviewsDTOS;

//        return reviewRepository.findAll().stream()
//                .map(review -> ReviewsDTO.builder()
//                        .id(review.getId())
//                        .bookId(review.getBookId())
//                        .memberId(review.getMemberId())
//                        .content(review.getContent())
//                        .rating(review.getRating())
//                        .reviewCommentDtos(review.getComments().stream()
//                                .map(ReviewCommentDto::new)
//                                .toList())
//                        .memberDtos(review.getRecommendMember().stream()
//                                .map(MemberDto::new)
//                                .toList())
//                        .build())
//                .collect(Collectors.toList());

        //리뷰 dto 안에 생성자를 만들어서 할 수 도 있다.
        //위 내용 벨로그에 정리하자
    }

    /**
     * 리뷰 생성
     * @param -- ReviewsDTO(rating,content,bookId,memberId)
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void write(ReviewsDTO reviewsDTO) {
        reviewRepository.save(Review.builder()
                        .bookId(reviewsDTO.getBookId())
                        .memberId(reviewsDTO.getMemberId())
                        .content(reviewsDTO.getContent())
                        .rating(reviewsDTO.getRating())
                        .recommendMember(new HashSet<>())
                    .build());

    }

    /**
     * 리뷰 수정
     * @param -- reviewsDTO(content,rating)
     * @param id - 리뷰 id
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void modify(ReviewsDTO reviewsDTO,Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                ));
        review.setContent(reviewsDTO.getContent());
        review.setRating(reviewsDTO.getRating());
        reviewRepository.save(review);
    }

    /**
     * 리뷰 삭제
     * @param -- id
     * @return ReviewDTO - id,content,memberId,bookId,rating
     *
     * @author 이광석
     * @since 25.01.27
     */
    public ReviewsDTO delete(Integer id) {
        Review review = reviewRepository.findById(id)
                        .orElseThrow(()-> new ReviewException(
                                ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                                ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                                ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                        ));
        reviewRepository.delete(review);

         return ReviewsDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .memberId(review.getMemberId())
                .bookId(review.getBookId())
                .rating(review.getRating())
                .build();

    }

    /**
     * 리뷰 추천/추천 취소
     * @param -- reviewId -- 리뷰 id
     * @param -- memberId -- 추천인 id
     * @return boolean - 추천(true)/추천 취소(false)
     *
     * @author 이광석
     * @since 25.01.27
     */
    public boolean recommend(Integer reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                ));

        Member member = memberRepository.findById(memberId)
                        .orElseThrow(()->new ReviewException(
                                ReviewErrorCode.MEMBER_NOT_FOUND.getStatus(),
                                ReviewErrorCode.MEMBER_NOT_FOUND.getErrorCode(),
                                ReviewErrorCode.MEMBER_NOT_FOUND.getMessage()
                        ));

        Set<Member> list = review.getRecommendMember();

        if (list.contains(member)) {
            list.remove(member);
            review.setRecommendMember(list);
            reviewRepository.save(review);
            return false;
        }else{
            list.add(member);
            review.setRecommendMember(list);
            reviewRepository.save(review);
            return true;
        }


    }


    /**
     * 단일 리뷰 검색
     * @param reviewId
     * @return ReviewsDTO - id,bookId,content,memberDtos
     *
     * @author 이광석
     * @since 25.02.03
     */
    public ReviewsDTO findById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()));

        ReviewsDTO reviewsDTO = new ReviewsDTO(review);
        return reviewsDTO;
    }





}
