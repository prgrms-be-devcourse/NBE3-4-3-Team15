package com.project.backend.domain.review.comment.entity;

import com.project.backend.domain.member.Member;
import com.project.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
public class ReviewComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer reviewId;

    private String userId;

    private String comment;

    private List<Member> recommend;

}
