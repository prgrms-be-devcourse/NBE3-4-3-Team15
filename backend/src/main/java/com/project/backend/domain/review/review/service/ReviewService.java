package com.project.backend.domain.review.review.service;


import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.service.FollowService;
import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.service.NotificationService;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.exception.ReviewErrorCode;
import com.project.backend.domain.review.exception.ReviewException;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
    private final MemberService memberService;
    private final NotificationService notificationService;
    private final FollowService followService;


    /**
     * 리뮤 전체 조회
     * @param page
     * @param size
     * @return List<ReviewsDTO>
     *
     * @author 이광석
     * @since 25.01.27
     */
    public Page<ReviewsDTO> findAll(int page,int size) {
        Pageable pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Review> pages = reviewRepository.findAll(pageable);
        List<ReviewsDTO> reviewsDTOList = pages.getContent()
                .stream()
                .map(ReviewsDTO::new)
                .collect(Collectors.toList());
        Page<ReviewsDTO> reviewsDTOPage = new PageImpl<>(reviewsDTOList,pageable,pages.getTotalElements());

        return reviewsDTOPage;

    }

    /**
     * 책id 기반 리뷰 조회
     * @param bookId
     * @param page
     * @param size
     * @return List<ReviewsDTO>
     *
     * @author 이광석
     * @since 25.02.07
     */
    public Page<ReviewsDTO> getBookIdReviews(Long bookId, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page-1,size,Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Review> pages = reviewRepository.findAllByBookId(bookId,pageable);
        List<ReviewsDTO> reviewsDTOList = reviewRepository.findAllByBookId(bookId,pageable)
                .stream()
                .map(ReviewsDTO::new)
                .collect(Collectors.toList());
        Page<ReviewsDTO> reviewsDTOPage = new PageImpl<>(reviewsDTOList,pageable,pages.getTotalElements());

        return reviewsDTOPage;

    }

    /**
     * userid 기반 리뷰 찾기
     * @param memberId
     * @return  List<ReviewsDTO>
     */
    public List<ReviewsDTO> getUserReviews(Long memberId) {

        List<ReviewsDTO> reviewsDTOS = reviewRepository.findAllByUserId(memberId);
        return reviewsDTOS;
    }


    /**
     * 리뷰 생성
     * @param memberId
     * @param reviewsDTO
     *
     * @author 이광석
     * @since 25.01.27
     */

        public void write(Long memberId,ReviewsDTO reviewsDTO){

        Review review =reviewRepository.save(Review.builder()
                        .userId(memberId)
                        .bookId(reviewsDTO.getBookId())
                        .content(reviewsDTO.getContent())
                        .rating(reviewsDTO.getRating())
                        .recommendMember(new HashSet<>())
                        .isDelete(false)
                    .build());


        MemberDto memberDto = memberService.getMemberById(memberId);   //리뷰 작성자
        List<FollowResponseDto> followers  = followService.getFollowers(memberDto.getUsername()); // 리뷰 작성자를 팔로우 하고 있는 팔로워 목록



        for(FollowResponseDto followDto: followers){
            MemberDto follower = memberService.getMyProfile(followDto.username());  // 리뷰 작성자를 팔로우 하는 팔로워
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .memberId(follower.getId())
                    .reviewId(review.getId())
                    .isCheck(false)
                    .content("리뷰가 작성되었습니다.")
                    .build();
            notificationService.create(notificationDTO);
        }
    }

    /**
     * 리뷰 수정
     * @param -- reviewsDTO(content,rating)
     * @param memberId
     *
     * @author 이광석
     * @since 25.01.27
     */
    public void modify(ReviewsDTO reviewsDTO,Long reviewId,Long memberId) {
        Review review = findById(reviewId);


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
    public ReviewsDTO delete(Long reviewId,Long memberId) {
        Review review = findById(reviewId);

        if(review.getComments().isEmpty()){
            reviewRepository.delete(review);
        }else {
            review.setContent("해당 댓글은 삭제 되었습니다");

            review.setDelete(true);
            reviewRepository.save(review);
        }

         return new ReviewsDTO(review);

    }

    /**
     * 리뷰 삭제 메소드
     * @param review
     *
     * @author 이광석
     * @since 25.02.11
     */
    public void reviewDelete(Review review){
        System.out.println("review1");
        reviewRepository.delete(review);
        System.out.println("review2");
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
     public boolean recommend(Long reviewId, Long memberId){
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
     * @return Review
     *
     * @author 이광석
     * @since 25.02.07
     */
    private Review findById(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getStatus(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getErrorCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage())
                );
    }


    /**
     * userDetails을 통해서 userId 추출
     * @param userDetails
     * @return Long
     *
     * @author 이광석
     * @since 25.02.10
     */
    public  Long myId(CustomUserDetails userDetails){
        return memberService.getMyProfile(userDetails.getUsername()).getId();
    }


    /**
     * 리뷰작성자와 현재 사용자가 같은지 확인
     * @param userDetails
     * @param reviewId
     *
     * @author 이광석
     * @since 25.02.10
     */
    public  void authorityCheck(CustomUserDetails userDetails,Long reviewId){
        Review review = findById(reviewId);
        Member member = memberRepository.findById(review.getUserId()).get(); // memberService로 변경 예정


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
