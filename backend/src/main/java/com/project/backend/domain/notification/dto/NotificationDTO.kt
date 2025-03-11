package com.project.backend.domain.notification.dto

import com.project.backend.domain.notification.entity.Notification
import com.project.backend.domain.notification.entity.NotificationType
import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long?,
    val producerMemberId: Long?,
    val consumerMemberId: Long?,
    val reviewId: Long?,
    val reviewCommentId: Long?,
    val isCheck: Boolean,
    val content: String,
    val notificationType: NotificationType,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(notification: Notification): NotificationDTO {
            return NotificationDTO(
                id = notification.id,
                producerMemberId = notification.producerMemberId,
                consumerMemberId = notification.consumerMemberId,
                reviewId = notification.reviewId,
                reviewCommentId = notification.reviewCommentId,
                isCheck = notification.isCheck,
                content = notification.content,
                notificationType = notification.notificationType,
                createdAt = notification.getCreatedAtSafe()
            )
        }
    }
}
