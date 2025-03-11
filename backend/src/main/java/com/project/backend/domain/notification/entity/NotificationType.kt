package com.project.backend.domain.notification.entity

enum class NotificationType(val message: String) {
    COMMENT("댓글을 작성하였습니다."),
    REPLY("대댓글을 작성하였습니다."),
    REVIEW("리뷰를 작성하였습니다.");
}
