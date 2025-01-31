package com.project.backend.domain.member;


import com.project.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

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
