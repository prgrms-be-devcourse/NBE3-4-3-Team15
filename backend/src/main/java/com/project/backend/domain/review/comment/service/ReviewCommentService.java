package com.project.backend.domain.review.comment.service;

import com.project.backend.domain.member.Member;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository;
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

    /**
     *
     * @param --  reviewId -- 리뷰 id
     * @return ReviewCommentDtoList
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public List<ReviewCommentDto> findByReview(Long reviewId) {
        return reviewCommentRepository.findAllByReviewId(reviewId).stream()
                .map(comment->ReviewCommentDto.builder()
                        .id(comment.getId())
                        .reviewId(comment.getReviewId())
                        .userId(comment.getUserId())
                        .comment(comment.getComment())
                        .recommendCount(comment.getRecommend().size())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
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
    public void write(Integer reviewId, ReviewCommentDto reviewCommentDto) {
        reviewCommentRepository.save(ReviewComment.builder()
                        .reviewId(reviewId)
                        .userId(reviewCommentDto.getUserId())
                        .comment(reviewCommentDto.getComment())
                        .recommend(new ArrayList<>())
                        .build());
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
    public void modify(Long reviewId, Integer commentId,ReviewCommentDto reviewCommentDto) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId).get();
        reviewComment.setComment(reviewCommentDto.getComment());
        reviewCommentRepository.save(reviewComment);
    }

    /**
     * 댓글 삭제
     * @param commentId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public void delete(Integer commentId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId).get();

        reviewCommentRepository.delete(reviewComment);
    }

    /**
     * 댓글 추천
     * @param commentId
     * @param memberId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public void recommend(Integer commentId,String memberId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId).get();

        //임시, memberRepository 생성시 수정
        Member member = new Member();
        List<Member> list = reviewComment.getRecommend();
        list.add(member);

        reviewComment.setRecommend(list);
        reviewCommentRepository.save(reviewComment);

    }
}
