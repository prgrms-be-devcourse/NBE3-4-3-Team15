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
     * @param memberId
     * @return List<ReviewCommentDto>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public List<ReviewCommentDto> findUserComment(Long memberId) {
        List<ReviewCommentDto> reviewCommentDtos = reviewCommentRepository.findAllByUserId(memberId);
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
    public ReviewCommentDto write(Long reviewId, ReviewCommentDto reviewCommentDto,long memberId) {  // 메소드가 너무 긴듯 분할 필요
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                ));
        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .userId(memberId)
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

        createCommentNotification(newReviewComment,review,reviewCommentDto);

        return new ReviewCommentDto(reviewComment);
    }

    /**
     * 코멘트 관련 알람 생성 메소드
     * @param reviewComment
     * @param review
     * @param reviewCommentDto
     *
     * @author 이광석
     * @since 25.02.10
     */
    public void createCommentNotification(ReviewComment reviewComment,Review review,ReviewCommentDto reviewCommentDto){
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .memberId(review.getUserId())
                .reviewComment(reviewComment.getId())
                .isCheck(false)
                .build();


        if(reviewCommentDto.getParentId()==null) {

            notificationDTO.setContent("nick", "COMMENT");
        }else{
            notificationDTO.setContent("nick", "REPLY");
        }

        notificationService.create(notificationDTO);
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
    public ReviewCommentDto modify(Long reviewId, Long commentId,ReviewCommentDto reviewCommentDto) {
        ReviewComment reviewComment = findCommentById(commentId);

        reviewComment.setComment(reviewCommentDto.getComment());
        reviewCommentRepository.save(reviewComment);

        return new ReviewCommentDto(reviewComment);
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @return ReviewCommendDto - id,commend, userId
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public ReviewCommentDto delete(Long reviewId,Long commentId) {
        ReviewComment reviewComment = findCommentById(commentId);

        if(reviewComment.getParent()!=null){ //대댓글인 경우
            deleteReply(reviewComment,reviewId);
        }else{ //댓글인 경우
            deleteComment(reviewComment,reviewId);
        }


        return new ReviewCommentDto(reviewComment);
    }

    /**
     * 댓글 추천
     * @param commentId
     * @param username
     * @return Boolean - 추천(true)/추천 취소(false)
     *
     * @author -- 이광석
     * @since -- 25.01.17
     */
    public Boolean recommend(Long commentId,String username) {
        ReviewComment reviewComment = findCommentById(commentId);
        Member member = memberRepository.findByUsername(username)

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
        ReviewComment reviewComment = findCommentById(commentId);

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
        ReviewComment parent = findCommentById(commentId);

        List<ReviewCommentDto> sons = reviewCommentRepository.findByParent(parent);
        return sons;
    }

    /**
     * 댓글 목록 조회
     * @param commentId
     * @return ReviewComment
     *
     * @author 이광석
     * @since 20.05.07
     */
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
    public Long myId(CustomUserDetails userDetails){
        return memberService.getMyProfile(userDetails.getUsername()).getId();
    }


    /**
     * 코멘트 작성자와 현재 사용자가 같은지 확인
     * @param username
     * @param commentUserId
     *
     * @author 이광석
     * @since 25.02.10
     */
    public void authorityCheck(String username, Long commentUserId){
        Member member = memberRepository.findById(commentUserId).get(); // memberService로 변경 예정


        if(!member.getUsername().equals(username))
        {
            throw new ReviewException(
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getStatus(),
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),
                    ReviewErrorCode.UNAUTHORIZED_ACCESS.getMessage()
            );
        }

    }

    /**
     * 코멘트 삭제 메소드
     * @param comment
     * @param reviewId
     *
     * @author 이광석
     * @since 25.02.11
     */
    public void deleteComment(ReviewComment comment,Long reviewId){

        Review review = reviewRepository.findById(reviewId).get();

        review.getComments().remove(comment);

        if(comment.getReplies().isEmpty()) {
            reviewCommentRepository.delete(comment);
            if(review.getComments().isEmpty()){
                reviewService.reviewDelete(review);
            }
        }else{
            comment.setDelete(true);
            comment.setComment("해당 댓글은 삭제되었습니다");
            reviewCommentRepository.save(comment);
        }


    }

    /**
     * 대댓글 삭제 메소드
     * @param reply
     * @param reviewId
     *
     * @author 이광석
     * @since 25.02.11
     */
    public void deleteReply(ReviewComment reply,Long reviewId){

        ReviewComment parent = reply.getParent();

        parent.getReplies().remove(reply); // 부모 대댓글 리스트에서 대댓글 삭제

        reviewCommentRepository.delete(reply); //대댓글 삭제


        //reviewCommentRepository.save(parent);
        if(parent.isDelete() && parent.getReplies().isEmpty()){
            deleteComment(parent,reviewId);
        }

    }
}
