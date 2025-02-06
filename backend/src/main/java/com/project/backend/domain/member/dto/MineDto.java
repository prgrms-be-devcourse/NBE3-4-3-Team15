package com.project.backend.domain.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 *
 * 회원 수정 DTO
 *
 * password, email, nickname, gender, birth
 * @author 손진영
 * @since 25. 1. 31.
 */
@Getter
public class MineDto {
    String password;

    @NotBlank
    @Column(unique = true)
    @Email
    String email;

    @NotBlank
    @Length(min = 2, max = 20)
    String nickname;

    int gender;

    LocalDate birth;
}
