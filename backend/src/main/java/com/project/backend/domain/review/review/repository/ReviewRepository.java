package com.project.backend.domain.review.review.repository;

import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.domain.review.review.reviewDTO.ReviewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 리뷰 레파지토리
 */
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllByUserId(Long userId);



    Page<Review> findAllByBookId(Long bookId, Pageable pageable);

    List<Review> findAllByUserIdOrderByBookIdDesc(Long userId);
}
