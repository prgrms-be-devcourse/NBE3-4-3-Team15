package com.project.backend.domain.notification.entity

import com.project.backend.global.baseEntity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val producerMemberId: Long,
    val consumerMemberId: Long,
    val reviewId: Long?,
    val reviewCommentId: Long?,

    var isCheck: Boolean = false,
    var content: String,

    @Enumerated(EnumType.STRING)
    val notificationType: NotificationType

    va
)
