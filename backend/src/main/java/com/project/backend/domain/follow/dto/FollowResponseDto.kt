package com.project.backend.domain.follow.dto;

/**
 * 팔로우 응답 DTO(record)
 * author: 이원재
 * date: 2025.01.31
 */
data class FollowResponseDto(
        val username: String,
        val nickname: String,
        val followerCount: Long,
        val followingCount: Long
) {}
