package com.project.backend.domain.follow.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 팔로우 요청 DTO(record)
 * author: 이원재
 * date: 2025.01.31
 */
public record FollowRequestDto(
        @NotNull(message = "팔로우할 회원 ID는 필수입니다.")
        Long followingId
) {}
