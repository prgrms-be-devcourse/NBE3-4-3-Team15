package com.project.backend.domain.challenge.entity;

import com.project.backend.domain.member.entity.Member;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * 챌린지 Entity
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String name;

    private String content;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    // 0: 대기, 1: 시작, 2: 종료
    private int status;

    private int totalDeposit;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members;
}
