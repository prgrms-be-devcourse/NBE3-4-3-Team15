package com.project.backend.domain.review.comment.service;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 리뷰 Service
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Service
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    /**
     *
     * @param --  reviewId -- 리뷰 id
     * @return ReviewCommentDtoList
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public List<ReviewCommentDto> findByReview(Integer reviewId) {
        return reviewCommentRepository.findAllByReviewId(reviewId).stream()
                .map(comment->ReviewCommentDto.builder()
                        .id(comment.getId())
                        .reviewId(comment.getReview().getId())
                        .userId(comment.getUserId())
                        .comment(comment.getComment())
                        .recommendCount(comment.getRecommend().size())
                        .build()
                ).collect(Collectors.toList());
    }

    /**
     * 댓글 생성
     * @param reviewId
     * @param reviewCommentDto
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto write(Integer reviewId, ReviewCommentDto reviewCommentDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new RuntimeException("리뷰를 찾을 수 없습니다"));


        ReviewComment reviewComment= reviewCommentRepository.save(ReviewComment.builder()
                        .review(review)
                        .userId(reviewCommentDto.getUserId())
                        .comment(reviewCommentDto.getComment())
                        .recommend(new ArrayList<>())
                        .build());
        return ReviewCommentDto.builder()
                .id(reviewComment.getId())
                .comment(reviewComment.getComment())
                .userId(reviewComment.getUserId())
                .build();
    }

    /**
     * 댓글 수정
     * @param reviewId
     * @param commentId
     * @param reviewCommentDto
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto modify(Long reviewId, Integer commentId,ReviewCommentDto reviewCommentDto) {

        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("코멘트를 찾을 수 없습니다"));
        reviewComment.setComment(reviewCommentDto.getComment());


        reviewCommentRepository.save(reviewComment);
        return ReviewCommentDto.builder()
                .id(reviewComment.getId())
                .comment(reviewComment.getComment())
                .build();
    }

    /**
     * 댓글 삭제
     * @param commentId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto delete(Integer commentId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("코멘트를 찾을 수 없습니다."));
        reviewCommentRepository.delete(reviewComment);
        return ReviewCommentDto.builder()
                .id(reviewComment.getId())
                .userId(reviewComment.getUserId())
                .comment(reviewComment.getComment())
                .build();
    }

    /**
     * 댓글 추천
     * @param commentId
     * @param memberId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public Boolean recommend(Integer commentId,String memberId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("해당 코맨트를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(()->new RuntimeException("해당 맴버를 찾을 수 없습니다."));

        List<Member> members  = reviewComment.getRecommend();
        if(members.contains(member)){
            members.remove(member);
            reviewComment.setRecommend(members);

            reviewCommentRepository.save(reviewComment);
            return false;
        }else{
            members.add(member);
            return true;
        }

    }

    public ReviewCommentDto findById(Integer commentId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId).orElseThrow(()->new RuntimeException("해당 코메트를 찾을 수 없습니다."));
        return ReviewCommentDto.builder()
                .id(reviewComment.getId())
                .userId(reviewComment.getUserId())
                .comment(reviewComment.getComment())
                .build();
    }
}
