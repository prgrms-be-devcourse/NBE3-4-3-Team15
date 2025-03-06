package com.project.backend.domain.challenge.attendance.entity;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;


/**
 *
 * 출석 Entity
 * 성공조건이 맞을 시 출석 데이터 저장
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
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private CheckType checkType;

    private long writeId;

    public enum CheckType {
        REVIEW,
        COMMENT
    }

    public static Optional<Attendance> createAttendance(Challenge challenge, Member member) {

        return member.findTodayReview()
                .map(review ->
                        Attendance.builder()
                                .challenge(challenge)
                                .member(member)
                                .checkType(Attendance.CheckType.REVIEW)
                                .writeId(review.getId())
                                .build()
                )
                .or(() -> member.findTodayComment()
                        .map(comment ->
                                Attendance.builder()
                                        .challenge(challenge)
                                        .member(member)
                                        .checkType(Attendance.CheckType.REVIEW)
                                        .writeId(comment.getId())
                                        .build()
                        )
                );
    }
}
