package com.project.backend.domain.member.entity;

import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.review.comment.entity.ReviewComment;
import com.project.backend.domain.review.review.entity.Review;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 *
 * 회원 Entity
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@Entity
@Getter
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

    @ManyToMany(mappedBy = "recommendMember")
    private Set<Review> recommendReviews;

    @ManyToMany(mappedBy = "recommend")
    private Set<ReviewComment> recommendReviewComments;

    @OneToMany(mappedBy = "following", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> followers;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> followings;

    public void updateMemberInfo(String email, int gender, String nickname, LocalDate birth) {
        this.email = email;
        this.gender = gender;
        this.nickname = nickname;
        this.birth = birth;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
