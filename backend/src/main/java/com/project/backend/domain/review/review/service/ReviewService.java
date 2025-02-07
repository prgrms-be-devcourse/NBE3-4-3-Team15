package com.project.backend.domain.review.review.service;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.review.exception.ReviewErrorCode;
import com.project.backend.domain.review.exception.ReviewException;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    public List<ReviewsDTO> findAll(int page,int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Review> ReviewPage = reviewRepository.findAll(pageable);

        List<ReviewsDTO> reviewsDTOS = ReviewPage.getContent().stream()
                .map(ReviewsDTO::new)
                .collect(Collectors.toList());
        return reviewsDTOS;

    }

    public List<ReviewsDTO> getBookIdReviews(Long bookId, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Review> reviewPage = reviewRepository.findAllByBookId(bookId,pageable);
        List<ReviewsDTO> reviewsDTOS = reviewPage.stream()
                .map(ReviewsDTO::new)
                .collect(Collectors.toList());
        return reviewsDTOS;

    }

    /**
     * userid 기반 리뷰 찾기
     * @param userId
     * @return  List<ReviewsDTO>
     */
    public List<ReviewsDTO> getUserReviews(Long userId) {
        List<ReviewsDTO> reviewsDTOS = reviewRepository.findAllByUserId(userId);
        return reviewsDTOS;
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
                        .userId(reviewsDTO.getUserId())
                        .bookId(reviewsDTO.getBookId())
                        .userId(reviewsDTO.getUserId())
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
    public void modify(ReviewsDTO reviewsDTO,Long id) {
        Review review = findById(id);
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
    public ReviewsDTO delete(Long id) {
        Review review = findById(id);

        reviewRepository.delete(review);

         return ReviewsDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .userId(review.getUserId())
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
    public boolean recommend(Long reviewId, Long memberId) {
        Review review = findById(reviewId);

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
    public ReviewsDTO getReview(Long reviewId) {
        Review review = findById(reviewId);

        ReviewsDTO reviewsDTO = new ReviewsDTO(review);
        return reviewsDTO;
    }


    /**
     * db에서 review 꺼내고 에러처리
     * @param reviewId
     * @return
     */
    private Review findById(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage())
                );
    }

}
