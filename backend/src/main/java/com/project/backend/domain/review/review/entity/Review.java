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
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookId;

    private String memberId;


    private String content;

    private Integer rating;

    private List<Member> listsMember;
}
