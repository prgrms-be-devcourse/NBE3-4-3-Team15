package com.project.backend.domain.notification.repository;

import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 알림 레파지토리
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {


    List<NotificationDTO> findAllByConsumerMemberId(Long id);
}
