package com.project.backend.domain.challenge.entity;

import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


/**
 *
 * 출석 Entity
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

    private long memberId;

    private long challengeId;

    private int checkType;
}
