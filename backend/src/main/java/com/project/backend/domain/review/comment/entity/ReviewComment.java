package com.project.backend.domain.review.comment.entity;

import com.project.backend.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 댓글 Entity
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Getter
@Setter
@Entity
public class ReviewComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer reviewId;

    private String userId;

    private String comment;

}
