package com.project.backend.domain.review.review.repository;

import com.project.backend.domain.review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
