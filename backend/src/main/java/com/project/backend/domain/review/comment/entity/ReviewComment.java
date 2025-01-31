package com.project.backend.domain.review.comment.entity;



import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    private String userId;

    private String comment;


    @PrePersist
    public void prePersist() {
        if (getCreatedAt() == null) {
            setCreatedAt(LocalDateTime.now());
        }
    }
//    private List<Member> recommend;

}
