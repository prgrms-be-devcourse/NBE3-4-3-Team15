package com.project.backend.domain.follow.dto;

/**
 * 팔로우 응답 DTO(record)
 * author: 이원재
 * date: 2025.01.31
 */
public record FollowResponseDto(
        String memberId,
        String nickname,
        Long followerCount,
        Long followingCount
) {}
