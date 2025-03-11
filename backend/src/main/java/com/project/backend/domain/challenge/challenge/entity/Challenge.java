package com.project.backend.domain.challenge.challenge.entity;

import com.project.backend.domain.challenge.entry.entity.Entry;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
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
        START,
        REFUNDING,
        END
    }

    /**
     * 총 예치금 증가
     *
     * @param deposit 증가할 예치금
     */
    public void plusDeposit(long deposit) {
        totalDeposit += deposit;
    }

    /**
     * 총 예치금 감소
     *
     * @param deposit 감소할 예치금
     */
    public void minusDeposit(long deposit) {
        totalDeposit -= deposit;
    }

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(this.startDate)) {
            this.status = ChallengeStatus.WAITING;
        } else if (now.isAfter(this.endDate)) {
            this.status = ChallengeStatus.END;
        } else {
            this.status = ChallengeStatus.START;
        }
    }

    /**
     * 챌린지 기간의 총 일수
     *
     * @return 총 일수
     */
    public int getTotalDays() {
        return (int) (endDate.toLocalDate().toEpochDay() -
                startDate.toLocalDate().toEpochDay() + 1);
    }
}
