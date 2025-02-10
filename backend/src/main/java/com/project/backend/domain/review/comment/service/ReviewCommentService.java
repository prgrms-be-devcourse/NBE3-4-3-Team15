package com.project.backend.domain.review.comment.service;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository;
import com.project.backend.domain.review.exception.ReviewErrorCode;
import com.project.backend.domain.review.exception.ReviewException;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.domain.review.review.service.ReviewService;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final MemberService memberService;

    /**
     * 리뷰 코멘트 목록 출력
     * @param --  reviewId -- 리뷰 id
     * @return ReviewCommentDtoList
     * @return  List<ReviewCommentDto> - id,reviewId, userId, comment, recommendCount
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public List<ReviewCommentDto> findComment(Long reviewId) {
        ReviewsDTO reviewsDTO = reviewService.getReview(reviewId);
        List<ReviewCommentDto> comments= reviewsDTO.getReviewCommentDtos().stream()
                .filter(comment->comment.getParentId()==null)
                .collect(Collectors.toList());
        return comments;
    }


    /**
     * userId기반 코멘트 검색
     * @param userDetails
     * @return List<ReviewCommentDto>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public List<ReviewCommentDto> findUserComment(CustomUserDetails userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).get(); //memberservice로 변경
        List<ReviewCommentDto> reviewCommentDtos = reviewCommentRepository.findAllByUserId(member.getId());
        return reviewCommentDtos;
    }

    /**
     * 댓글 생성
     * @param reviewId
     * @param reviewCommentDto
     * @return ReviewCommentDto - id,commend, userId,
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto write(Long reviewId, ReviewCommentDto reviewCommentDto,CustomUserDetails userDetails) {  // 메소드가 너무 긴듯 분할 필요
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                ));
        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .userId(myId(userDetails))
                .comment(reviewCommentDto.getComment())
                .recommend(new HashSet<>())
                .depth(0)
                .isDelete(false)
                .build();

        if(reviewCommentDto.getParentId()!=null){
            ReviewComment parentComment = findCommentById(reviewCommentDto.getParentId());

            if(parentComment.getDepth()+1>=2){
               throw new ReviewException(
                       ReviewErrorCode.INVALID_COMMENT_DEPTH.getStatus(),
                       ReviewErrorCode.INVALID_COMMENT_DEPTH.getErrorCode(),
                       ReviewErrorCode.INVALID_COMMENT_DEPTH.getMessage()
               );
            }
            reviewComment.setParent(parentComment);
            reviewComment.setDepth(parentComment.getDepth()+1);
        }

        ReviewComment newReviewComment = reviewCommentRepository.save(reviewComment);



        NotificationDTO notificationDTO = NotificationDTO.builder()
                .memberId(review.getUserId())
                .reviewComment(newReviewComment.getId())
                .isCheck(false)
                .build();


        if(reviewCommentDto.getParentId()==null) {

            notificationDTO.setContent("nick", "COMMENT");
        }else{
            notificationDTO.setContent("nick", "REPLY");
        }

        notificationService.create(notificationDTO);

        return new ReviewCommentDto(reviewComment);
    }

    /**
     * 댓글 수정
     * @param reviewId
     * @param commentId
     * @param reviewCommentDto
     * @return ReviewCommentDto - id, comment, userId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto modify(Long reviewId, Long commentId,ReviewCommentDto reviewCommentDto, CustomUserDetails userDetails) {


        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
                ));

        authorityCheck(userDetails,reviewComment);
        reviewComment.setComment(reviewCommentDto.getComment());
        reviewCommentRepository.save(reviewComment);

        return new ReviewCommentDto(reviewComment);
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @return RevieCommendDto - id,commend, userId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto delete(Long commentId,CustomUserDetails userDetails) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
                ));

        authorityCheck(userDetails,reviewComment);
        if(reviewComment.getParent()!=null){
            ReviewComment parent = reviewComment.getParent();

            reviewCommentRepository.delete(reviewComment);

            if(parent.isDelete()){
                reviewCommentRepository.delete(parent);
            }
        }else{
            if(reviewComment.getReplies().size()==0|| reviewComment.getReplies()==null){
                reviewCommentRepository.delete(reviewComment);
            }else{
                reviewComment.setComment("해당 코멘트는 삭제되었습니다. ");
                reviewComment.setUserId(null);
                reviewComment.setDelete(true);
                reviewCommentRepository.save(reviewComment);
            }
        }

        return new ReviewCommentDto(reviewComment);


    }

    /**
     * 댓글 추천
     * @param commentId
     * @param userDetails
     * @return Boolean - 추천(true)/추천 취소(false)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public Boolean recommend(Long commentId,CustomUserDetails userDetails) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
                ));
        Member member = memberRepository.findByUsername(userDetails.getUsername())
                        .orElseThrow(()->new ReviewException(
                                ReviewErrorCode.MEMBER_NOT_FOUND.getStatus(),
                                ReviewErrorCode.MEMBER_NOT_FOUND.getErrorCode(),
                                ReviewErrorCode.MEMBER_NOT_FOUND.getMessage()
                        ));

        Set<Member> members  = reviewComment.getRecommend();
        if(members.contains(member)){
            members.remove(member);
            reviewComment.setRecommend(members);

            reviewCommentRepository.save(reviewComment);
            return false;
        }else{
            members.add(member);
            reviewComment.setRecommend(members);
            reviewCommentRepository.save(reviewComment);
            return true;
        }

    }

    /**
     *단일 코멘트 찾기
     * @param commentId
     * @return ReviewCommentDto - id, comment, userId
     *
     * @author 이광석
     * @since 25.02.03
     */
    public ReviewCommentDto findById(Long commentId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
        ));

        return new ReviewCommentDto(reviewComment);
    }


    /**
     * 대댓글 출력
     * @param commentId
     * @return List<ReviewCommentDto>
     *
     * @since 25.02.05
     * @author 이광석
     */
    public List<ReviewCommentDto> findReplies(Long commentId) {
        ReviewComment parent = reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
                ));
        List<ReviewCommentDto> sons = reviewCommentRepository.findByParent(parent);
        return sons;
    }

    private ReviewComment findCommentById (Long commentId){
        return reviewCommentRepository.findById(commentId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.COMMENT_NOT_FOUND.getStatus(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.COMMENT_NOT_FOUND.getMessage()
                ));
    }


    /**
     * userDetails의 username 을 이용해서 userId 추출;
     * @param userDetails
     * @return Long
     *
     * @author 이광석
     * @since 25.02.10
     */
    private Long myId(CustomUserDetails userDetails){
        return memberService.getMyProfile(userDetails.getUsername()).getId();
    }


    /**
     * 코멘트 작성자와 현재 사용자가 같은지 확인
     * @param userDetails
     * @param comment
     *
     * @author 이광석
     * @since 25.02.10
     */
    private void authorityCheck(CustomUserDetails userDetails, ReviewComment comment){
        Member member = memberRepository.findById(comment.getUserId()).get(); // memberService로 변경 예정


        if(!member.getUsername().equals(userDetails.getUsername()))
        {
            throw new ReviewException(
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getStatus(),
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getMessage()
            );
        }

    }
}
