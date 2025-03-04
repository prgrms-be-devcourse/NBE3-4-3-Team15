package com.project.backend.domain.challenge.challenge.entity;

import com.project.backend.domain.challenge.entry.entity.Entry;
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

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private long totalDeposit;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries;

    public enum ChallengeStatus {
        WAITING,
        STARTED,
        ENDED
    }
}
