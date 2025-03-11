package com.project.backend.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingDTO {
    private int rank;
    private String title;
    private String content;
    private double score;
}
