package com.project.backend.domain.notification.service

import com.project.backend.domain.member.dto.MemberDto
import com.project.backend.domain.notification.dto.NotificationDTO
import com.project.backend.domain.notification.entity.Notification
import com.project.backend.domain.notification.entity.NotificationType
import com.project.backend.domain.notification.exception.NotificationErrorCode
import com.project.backend.domain.notification.exception.NotificationException
import com.project.backend.domain.notification.repository.NotificationRepository
import com.project.backend.global.redis.service.RedisPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val redisPublisher: RedisPublisher
) {

    fun buildContent(username: String, type: NotificationType): String {
        return "$username 님이 ${type.message}"
    }

    /**
     * 알람 생성
     */
    fun create(notificationDTO: NotificationDTO): NotificationDTO {
        val notification = Notification(
            consumerMemberId = notificationDTO.consumerMemberId!!,
            producerMemberId = notificationDTO.producerMemberId!!,
            reviewId = notificationDTO.reviewId,
            reviewCommentId = notificationDTO.reviewCommentId,
            isCheck = notificationDTO.isCheck,
            content = notificationDTO.content,
            notificationType = notificationDTO.notificationType
        )

        redisPublisher.publishToUser(notification.consumerMemberId, notification.content)

        return NotificationDTO.from(notificationRepository.save(notification))
    }

    /**
     * 알람 조회
     */
    fun findByUser(memberDto: MemberDto, page: Int, size: Int, onlyNotCheck: Boolean): Page<NotificationDTO> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val notificationPage = if (onlyNotCheck) {
            notificationRepository.findAllByConsumerMemberIdAndIsCheckFalse(memberDto.id, pageable)
        } else {
            notificationRepository.findAllByConsumerMemberId(memberDto.id, pageable)
        }

        return notificationPage.map { NotificationDTO.from(it) }
    }

    /**
     * 알람 읽음 상태 변경
     */
    @Transactional
    fun notificationCheck(notificationId: Long, memberDto: MemberDto) {
        val notification = findNotificationById(notificationId).apply { isCheck = true }
        authorityCheck(memberDto, notification)
        notificationRepository.save(notification)
    }

    /**
     * 알람 삭제
     */
    @Transactional
    fun deleteNotification(notificationId: Long, memberDto: MemberDto) {
        val notification = findNotificationById(notificationId)
        authorityCheck(memberDto, notification)
        notificationRepository.delete(notification)
    }

    /**
     * Notification 탐색
     */
    private fun findNotificationById(notificationId: Long): Notification {
        return notificationRepository.findById(notificationId)
            .orElseThrow { NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND) }
    }

    /**
     * 로그인 된 사용자와 알림 member가 같은지 확인
     */
    private fun authorityCheck(memberDto: MemberDto, notification: Notification) {
        if (notification.consumerMemberId != memberDto.id) {
            throw NotificationException(NotificationErrorCode.UNAUTHORIZED_ACCESS)
        }
    }

    /**
     * 전체 알림 개수 조회
     */
    fun getNotificationTotalCount(memberDto: MemberDto): Long {
        return notificationRepository.countByConsumerMemberId(memberDto.id)
    }
}
