package com.project.backend.domain.review.review.service;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        return reviewRepository.findAll().stream()
                .map(review -> ReviewsDTO.builder()
                        .id(review.getId())
                        .bookId(review.getBookId())
                        .memberId(review.getMemberId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .recommendCount(review.getRecommendMember().size())
                        .build())
                .collect(Collectors.toList());
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
                        .recommendMember(new ArrayList<>())
                    .build());

    }

    /**
     * 리뷰 수정
     * @param -- reviewsDTO(content,rating)
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void modify(ReviewsDTO reviewsDTO,Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()->new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setContent(reviewsDTO.getContent());
        review.setRating(reviewsDTO.getRating());
        reviewRepository.save(review);
    }

    /**
     * 리뷰 삭제
     * @param -- id
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void delete(Integer id) {
        Review review = reviewRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("리뷰를 찾을 수 없다"));
        reviewRepository.delete(review);
    }

    /**
     * 리뷰 추천/추천 취소
     * @param -- reviewId -- 리뷰 id
     * @param -- memberId -- 추천인 id
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void recommend(Integer reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new RuntimeException("해당 리뷰를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                        .orElseThrow(()->new RuntimeException("해당 맴버를 찾을 수 없습니다."));

        List<Member> list = review.getRecommendMember();

        if (list.contains(member)) {
            list.remove(member);
        }else{
            list.add(member);
        }

        review.setRecommendMember(list);
        reviewRepository.save(review);
    }
}
