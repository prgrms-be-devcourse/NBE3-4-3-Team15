package com.project.backend.domain.member.entity;

import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

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
    private String id;

    private String password;

    private String email;

    private int gender;

    private String nickname;

    private LocalDate birth;
}
