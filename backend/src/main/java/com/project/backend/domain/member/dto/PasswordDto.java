package com.project.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 *
 * 비밀번호 DTO
 *
 * password
 * @author 손진영
 * @since 25. 1. 31.
 */
@Getter
public class PasswordDto {
    @NotBlank
    String password;
}
