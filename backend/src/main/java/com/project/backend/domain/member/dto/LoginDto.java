package com.project.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 *
 * 로그인 DTO
 *
 * id, password
 * @author 손진영
 * @since 25. 1. 31.
 */
@Getter
public class LoginDto extends PasswordDto {
    @NotBlank
    String id;
}
