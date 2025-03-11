package com.project.backend.domain.review.review.repository;

import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 리뷰 레파지토리
 */
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<ReviewsDTO> findAllByUserId(Long userId);



    Page<Review> findAllByBookId(Long bookId, Pageable pageable);

    List<Review> findAllByUserIdOrderByBookIdDesc(Long userId);

    /**
     * 특정 기간 동안 작성된 리뷰 수를 도서별로 집계하여 조회하는 메서드
     *
     * @param start 조회 시작 날짜
     * @param end 조회 종료 날짜
     * @return 각 도서의 ID와 해당 기간 동안 작성된 리뷰 수를 포함한 리스트
     *
     * @author 김남우
     * @since 2025.03.06
     */
    @Query("SELECT r.bookId, COUNT(r.id) FROM Review r WHERE r.createdAt BETWEEN :start AND :end GROUP BY r.bookId")
    List<Object[]> findReviewCounts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
