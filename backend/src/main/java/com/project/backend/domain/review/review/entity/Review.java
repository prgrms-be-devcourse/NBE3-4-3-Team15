package com.project.backend.domain.review.review.entity;


import com.project.backend.domain.member.Member;
import com.project.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;



/**
 * 리뷰 dto
 */
@Entity
@Getter
@Setter
@Builder
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bookId;

    private String memberId;

    private String content;

    private Integer rating;


    @OneToMany
   // @JoinColumn(name="")
    private List<Member> recommendMember;


}
