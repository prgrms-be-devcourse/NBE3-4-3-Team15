package com.project.backend.domain.notification.exception

import com.project.backend.global.response.GenericResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.project.backend.domain.notification"])
class NotificationExceptionHandler {

    @ExceptionHandler(NotificationException::class)
    fun handleNotificationException(ex: NotificationException): ResponseEntity<GenericResponse<String>> {
        return ResponseEntity
            .status(ex.status)
            .body(GenericResponse.of(ex.errorCode, ex.message))
    }
}
