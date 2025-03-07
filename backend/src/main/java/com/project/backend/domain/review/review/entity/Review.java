package com.project.backend.domain.review.review.entity;


import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


/**
 * 리뷰
 *
 * @author 이광석
 * @since 25.02.04
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long bookId;

    @NotNull
    private Long userId;

    @NotBlank
    private String content;

    @Min(0)
    @Max(10)
    @NotNull
    private Integer rating;

    @OneToMany(mappedBy = "review",cascade =CascadeType.ALL,orphanRemoval = true)
    private List<ReviewComment> comments;

    @ManyToMany
    private Set<Member> recommendMember;

    boolean isDelete;


}
