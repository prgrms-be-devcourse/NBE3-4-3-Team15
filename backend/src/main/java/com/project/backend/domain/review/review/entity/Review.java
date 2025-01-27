package com.project.backend.domain.review.review.entity;


import com.project.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    private Integer id;

    private String bookId;

    private String memberId;


    private String content;

    private Integer rating;

    private String likesMembers;
}
