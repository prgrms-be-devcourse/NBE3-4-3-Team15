package com.project.backend.domain.member.entity;

import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.domain.follow.entity.Follow;
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

    private long deposit;

    @ManyToMany(mappedBy = "recommendMember")
    private List<Review> recommendReviews;

    @ManyToMany(mappedBy = "recommend")
    private List<ReviewComment> recommendReviewComments;

    @OneToMany(mappedBy = "following", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> followers;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> followings;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries;

    public void updateMemberInfo(String email, int gender, String nickname, LocalDate birth) {
        this.email = email;
        this.gender = gender;
        this.nickname = nickname;
        this.birth = birth;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void plusDeposit(long deposit) {
        this.deposit += deposit;
    }

    public void minusDeposit(long deposit) {
        this.deposit -= deposit;
    }
}
