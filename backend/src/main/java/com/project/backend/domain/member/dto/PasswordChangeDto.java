package com.project.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    @NotBlank(message = "현재 비밀번호를 입력해야 합니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해야 합니다.")
    @Size(min = 8, message = "새 비밀번호는 최소 8자리 이상이어야 합니다.")
    private String newPassword;
}
