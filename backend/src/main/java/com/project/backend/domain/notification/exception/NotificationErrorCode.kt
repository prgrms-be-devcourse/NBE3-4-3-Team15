package com.project.backend.domain.notification.exception

import org.springframework.http.HttpStatus

enum class NotificationErrorCode(
    val status: HttpStatus,
    val errorCode: String,
    val message: String
) {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알람을 찾을 수 없습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS", "권한이 없습니다");
}
