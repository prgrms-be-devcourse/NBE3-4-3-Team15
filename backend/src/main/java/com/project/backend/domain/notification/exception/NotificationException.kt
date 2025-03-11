package com.project.backend.domain.notification.exception

import org.springframework.http.HttpStatus

class NotificationException(
    val status: HttpStatus,
    val errorCode: String,
    override val message: String
) : RuntimeException(message) {
    constructor(errorCode: NotificationErrorCode) : this(
        status = errorCode.status,
        errorCode = errorCode.errorCode,
        message = errorCode.message
    )
}
