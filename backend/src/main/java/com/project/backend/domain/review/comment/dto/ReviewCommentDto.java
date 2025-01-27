package com.project.backend.domain.review.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 *
 * 댓글 DTO
 *
 * @author shjung
 * @since 25. 1. 24.
 */
@Getter
@Setter
@Builder

public class ReviewCommentDto {

    private Integer id;

    @NotNull
    private Integer reviewId;

    @NotBlank
    private String userId;

    @NotBlank
    private String comment;

    private Integer recommendCount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
