package com.project.backend.domain.review.comment.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    private Long userId;

    private String comment;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="parent_id")
    private ReviewComment parent;


    private Integer depth =0;

    @ManyToMany
    private Set<Member> recommend;


}
