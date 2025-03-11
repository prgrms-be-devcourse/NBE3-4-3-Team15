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
    public String username;

    private String password;

    @Column(unique = true)
    private String email;

    private int gender;

    public String nickname;

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

    /**
     * -- Member 엔티티의 ID 접근을 위한 임시 Getter --
     *
     * @author -- 김남우 --
     * @since -- 3월 4일 --
     *
     * Kotlin의 Book 엔티티와 Java의 Member 엔티티 간 ID 접근 문제 해결을 위해 임시로 직접 getter 추가
     * Member도 Kotlin으로 마이그레이션되면 삭제 가능
     */
    public Long getId() {
        return id;
    }

    public String getUserNameK(){return this.username;}
}
