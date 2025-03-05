package com.project.backend.domain.challenge.challenge.dto;


import com.project.backend.domain.challenge.challenge.entity.Challenge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChallengeDto {

    @NotBlank
    @Length(min = 2, max = 16)
    private String name;

    @NotBlank
    @Length(min = 2, max = 200)
    private String content;

    @NotNull(message = "날짜를 지정해야 합니다.")
    private LocalDateTime startDate;

    @NotNull(message = "날짜를 지정해야 합니다.")
    private LocalDateTime endDate;

    private Challenge.ChallengeStatus status;

    public ChallengeDto(Challenge challenge) {
        this.name = challenge.getName();
        this.content = challenge.getContent();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.status = challenge.getStatus();
    }

}
