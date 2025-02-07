package com.project.backend.domain.member.entity;

import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * 회원 Entity
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(unique = true, length = 30)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private int gender;

    private String nickname;

    private LocalDate birth;

    @ManyToMany
    private List<Review> recommendReviews;

    @ManyToMany
    private List<ReviewComment> reviewComments;
}
