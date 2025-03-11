package com.project.backend.domain.notification.repository

import com.project.backend.domain.notification.entity.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 알림 레포지토리
 */
interface NotificationRepository : JpaRepository<Notification, Long> {

    fun findAllByConsumerMemberId(id: Long): List<Notification>

    fun findAllByConsumerMemberId(id: Long, pageable: Pageable): Page<Notification>

    fun findAllByConsumerMemberIdAndIsCheckFalse(id: Long, pageable: Pageable): Page<Notification>

    fun countByConsumerMemberId(memberId: Long): Long
}
