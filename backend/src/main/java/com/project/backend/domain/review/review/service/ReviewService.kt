package com.project.backend.domain.review.review.service

import com.project.backend.domain.follow.service.FollowService
import com.project.backend.domain.member.entity.Member
import com.project.backend.domain.member.repository.MemberRepository
import com.project.backend.domain.member.service.MemberService
import com.project.backend.domain.notification.dto.NotificationDTO
import com.project.backend.domain.notification.entity.NotificationType
import com.project.backend.domain.notification.service.NotificationService
import com.project.backend.domain.review.exception.ReviewErrorCode
import com.project.backend.domain.review.exception.ReviewException
import com.project.backend.domain.review.review.entity.Review
import com.project.backend.domain.review.review.repository.ReviewRepository
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO
import com.project.backend.global.authority.CustomUserDetails
import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.stream.Collectors


/**
 * 리뷰 서비스
 *
 * @author 이광석
 * @since 25.01.27
 */
@Service
@RequiredArgsConstructor
class ReviewService(  private val reviewRepository: ReviewRepository,
                      private val memberRepository: MemberRepository,
                      private val memberService: MemberService,
                      private val notificationService: NotificationService,
                      private val followService: FollowService) {



    /**
     * 리뮤 전체 조회
     * @param page
     * @param size
     * @return List<ReviewsDTO>
     *
     * @author 이광석
     * @since 25.01.27
    </ReviewsDTO> */
    fun findAll(page: Int, size: Int): Page<ReviewsDTO> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val pages = reviewRepository.findAll(pageable)
        val reviewsDTOList = pages.content.map{ReviewsDTO.from(it)}
        val reviewsDTOPage: Page<ReviewsDTO> = PageImpl(reviewsDTOList, pageable, pages.totalElements)

        return reviewsDTOPage
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
    </ReviewsDTO> */
    fun getBookIdReviews(bookId: Long, page: Int, size: Int): Page<ReviewsDTO> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pages = reviewRepository.findAllByBookId(bookId, pageable)
        val reviewsDTOList = pages.content.map { ReviewsDTO.from(it) }
        val reviewsDTOPage: Page<ReviewsDTO> = PageImpl(reviewsDTOList, pageable, pages.totalElements)

        return reviewsDTOPage
    }

    /**
     * userid 기반 리뷰 찾기
     * @param memberId
     * @return  List<ReviewsDTO>
    </ReviewsDTO> */
    fun getUserReviews(memberId: Long): List<ReviewsDTO> {
        return reviewRepository.findAllByUserId(memberId).map { ReviewsDTO.from(it) }

    }


    /**
     * 리뷰 생성
     * @param memberId
     * @param reviewsDTO
     *
     * @author 이광석
     * @since 25.01.27
     */
    @Transactional
    fun write(memberId: Long, reviewsDTO: ReviewsDTO) {
       val review= Review(
           bookId = reviewsDTO.bookId,
           userId = memberId,
           content = reviewsDTO.content,
           rating = reviewsDTO.rating,
           isDelete = false
       )


        val memberDto = memberService.getMemberById(memberId) //리뷰 작성자
        val followers = followService.getFollowers(memberDto.username) // 리뷰 작성자를 팔로우 하고 있는 팔로워 목록



        for (followDto in followers) {
            val follower = memberService.getMyProfile(followDto.username) // 리뷰 작성자를 팔로우 하는 팔로워
            val notificationDTO = NotificationDTO(
                producerMemberId=memberId,
                consumerMemberId=follower.id,
                reviewId=review.id,
                isCheck=false,
                content=notificationService.buildContent(memberDto.username, NotificationType.REVIEW),
                notificationType=NotificationType.REVIEW,
                createdAt = LocalDateTime.now(),
                id = null,
                reviewCommentId = null
            )


            notificationService.create(notificationDTO)
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
    @Transactional
    fun modify(reviewsDTO: ReviewsDTO, reviewId: Long, memberId: Long) {
        val review = findById(reviewId)
        review.content = reviewsDTO.content
        review.rating = reviewsDTO.rating
        reviewRepository.save(review)
    }

    /**
     * 리뷰 삭제
     * @param -- id
     * @return ReviewDTO - id,content,memberId,bookId,rating
     *
     * @author 이광석
     * @since 25.01.27
     */
    @Transactional
    fun delete(reviewId: Long, memberId: Long?): ReviewsDTO {
        val review = findById(reviewId)
        if(review.comments.isEmpty()){
            reviewRepository.delete(review)
        }else{
            review.content ="해당 댓글은 삭제 되었습니다."
            review.isDelete = true
            reviewRepository.save(review)
        }

        return ReviewsDTO.from(review)
    }

    /**
     * 리뷰 삭제 메소드
     * @param review
     *
     * @author 이광석
     * @since 25.02.11
     */
    fun reviewDelete(review: Review) {
        println("review1")
        reviewRepository.delete(review)
        println("review2")
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
    fun recommend(reviewId: Long, memberId: Long): Boolean {
        val review = findById(reviewId)

        val member = memberRepository.findById(memberId)
            .orElseThrow {
                ReviewException(
                    ReviewErrorCode.MEMBER_NOT_FOUND.status,
                    ReviewErrorCode.MEMBER_NOT_FOUND.errorCode,
                    ReviewErrorCode.MEMBER_NOT_FOUND.message
                )
            }

        return if (review.recommendMember.contains(member)) {
            review.recommendMember.remove(member)
            reviewRepository.save(review)
            false
        } else {
            review.recommendMember.add(member)
            reviewRepository.save(review)
            true
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
    fun getReview(reviewId: Long): ReviewsDTO {
        val reviewsDTO = ReviewsDTO.from(findById(reviewId))
        return reviewsDTO
    }


    /**
     * db에서 review 꺼내고 에러처리
     * @param reviewId
     * @return Review
     *
     * @author 이광석
     * @since 25.02.07
     */
    private fun findById(reviewId: Long): Review {
        return reviewRepository.findById(reviewId)
            .orElseThrow {
                ReviewException(
                    ReviewErrorCode.REVIEW_NOT_FOUND.status,
                    ReviewErrorCode.REVIEW_NOT_FOUND.errorCode,
                    ReviewErrorCode.REVIEW_NOT_FOUND.message
                )
            }
    }


    /**
     * userDetails을 통해서 userId 추출
     * @param userDetails
     * @return Long
     *
     * @author 이광석
     * @since 25.02.10
     */
    fun myId(userDetails: CustomUserDetails): Long {
        return memberService.getMyProfile(userDetails.username).id
    }


    /**
     * 리뷰작성자와 현재 사용자가 같은지 확인
     * @param userDetails
     * @param reviewId
     *
     * @author 이광석
     * @since 25.02.10
     */
    fun authorityCheck(userDetails: CustomUserDetails, reviewId: Long) {
        val review = findById(reviewId)
        val member = memberRepository.findById(review.userId)
            .orElseThrow {
                ReviewException(
                    ReviewErrorCode.MEMBER_NOT_FOUND.status,
                    ReviewErrorCode.MEMBER_NOT_FOUND.errorCode,
                    ReviewErrorCode.MEMBER_NOT_FOUND.message
                )
            }
        if (member.username != userDetails.username) {
            throw ReviewException(
                ReviewErrorCode.UNAUTHORIZED_ACCESS.status,
                ReviewErrorCode.UNAUTHORIZED_ACCESS.errorCode,
                ReviewErrorCode.UNAUTHORIZED_ACCESS.message
            )
        }
    }

    /**
     *
     * 유저의 최신 리뷰 3개의 책 id 조회
     *
     * @param userId
     * @return
     *
     * @author shjung
     * @since 25. 3. 5.
     */
    fun getBookIds(userId: Long): List<Long> {
        return reviewRepository.findAllByUserIdOrderByBookIdDesc(userId)
            .map{it.bookId}
            .take(3)
    }
}
