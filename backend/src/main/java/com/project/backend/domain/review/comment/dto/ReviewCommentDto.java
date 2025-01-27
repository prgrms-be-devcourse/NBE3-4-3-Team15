package com.project.backend.domain.review.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
public class ReviewCommentDto {

    private Integer id;

    @NotBlank
    private Integer reviewId;

    @NotBlank
    private String userId;

    @NotBlank
    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
