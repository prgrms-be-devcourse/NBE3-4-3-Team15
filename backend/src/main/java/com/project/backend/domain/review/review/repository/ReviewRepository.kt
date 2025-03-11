package com.project.backend.domain.review.review.repository

import com.project.backend.domain.review.review.entity.Review
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable

interface ReviewRepository : JpaRepository<Review,Long>{
    fun findAllByUserId(userId: Long): List<Review>
    fun findAllByBookId(bookId: Long,pageable : Pageable): Page<Review>
    fun findAllByUserIdOrderByBookIdDesc(userId: Long): List<Review>
}