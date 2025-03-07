package com.project.backend.domain.review.comment.entity;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    @JoinColumn(name="parent_id")
    private ReviewComment parent;


    private Integer depth =0;

    @ManyToMany
    private Set<Member> recommend;

    private boolean isDelete;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> replies = new ArrayList<>();  // 자식 댓글 (대댓글)


}