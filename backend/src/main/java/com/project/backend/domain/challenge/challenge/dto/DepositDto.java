package com.project.backend.domain.challenge.challenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DepositDto {

    @NotNull(message = "금액을 등록하세요")
    long deposit;
}
