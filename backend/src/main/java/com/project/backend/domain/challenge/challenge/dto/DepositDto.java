package com.project.backend.domain.challenge.challenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 예치금 dto
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Getter
public class DepositDto {

    @NotNull(message = "금액을 등록하세요")
    long deposit;
}
