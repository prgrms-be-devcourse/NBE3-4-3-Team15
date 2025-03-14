package com.project.backend.domain.challenge.challenge.dto;


import com.project.backend.domain.challenge.challenge.entity.Challenge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 챌린지 DTO
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Getter
@NoArgsConstructor
public class ChallengeDto {

    private long id;

    @NotBlank
    @Length(min = 2, max = 16)
    private String name;

    @NotBlank
    @Length(min = 2, max = 200)
    private String content;

    @NotNull(message = "날짜를 지정해야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "날짜를 지정해야 합니다.")
    private LocalDate endDate;

    private Challenge.ChallengeStatus status;

    private long totalDeposit;

    public ChallengeDto(Challenge challenge) {
        this.id = challenge.getId();
        this.name = challenge.getName();
        this.content = challenge.getContent();
        this.startDate = challenge.getStartDate().toLocalDate();
        this.endDate = challenge.getEndDate().toLocalDate();
        this.status = challenge.getStatus();
        this.totalDeposit = challenge.getTotalDeposit();
    }

}
