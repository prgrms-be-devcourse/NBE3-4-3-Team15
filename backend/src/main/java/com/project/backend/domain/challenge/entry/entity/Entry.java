package com.project.backend.domain.challenge.entry.entity;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 *
 * 챌린지 참가 Entity
 * 챌린지와 멤버 간의 참가 관계 (예치금 조작)
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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"challenge_id", "member_id"})
})
public class Entry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private long deposit;

    private boolean isActive;

    private long refundAmount;  // 환급 금액

    private long rewardAmount;  // 추가 보상 금액

    private boolean refunded;   // 환급 여부

    private double rate; // 참여율

    public void updateRate(long attendanceCount, int totalDay) {
        this.rate = (attendanceCount / (double) totalDay) * 100;
    }
}
