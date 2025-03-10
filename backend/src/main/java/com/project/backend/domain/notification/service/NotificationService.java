package com.project.backend.domain.notification.service;


import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.entity.NotificationType;
import com.project.backend.domain.notification.exception.NotificationErrorCode;
import com.project.backend.domain.notification.exception.NotificationException;
import com.project.backend.domain.notification.repository.NotificationRepository;
import com.project.backend.global.redis.service.RedisPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 알람 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RedisPublisher redisPublisher;

    public String buildContent(String username, NotificationType type){
        return username + "님이 "+type.getMessage();
    }

    /**
     * 알람 생성
     * @param notificationDTO
     * @return NotificationDTO
     *
     * @author 이광석
     * @since 25.02.06
     */
    public NotificationDTO create(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .consumerMemberId(notificationDTO.getConsumerMemberId())
                .producerMemberId(notificationDTO.getProducerMemberId())
                .reviewId(notificationDTO.getReviewId())
                .reviewCommentId(notificationDTO.getReviewCommentId())
                .isCheck(notificationDTO.isCheck())
                .content(notificationDTO.getContent())
                .notificationType(notificationDTO.getNotificationType())
                .build();

        redisPublisher.publishToUser(notification.getConsumerMemberId(),notification.getContent());

        return new NotificationDTO(notificationRepository.save(notification));
    }

    /**
     * 알람 조회
     * @param memberDto - 클라이언트 memberDTO
     * @return List<NotificationDTO>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public Page<NotificationDTO> findByUser(MemberDto memberDto,int page, int size,boolean onlyNotCheck) {
        Pageable pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Notification> notificationPage;

        if(onlyNotCheck){
            notificationPage = notificationRepository.findAllByConsumerMemberIdAndIsCheckFalse(memberDto.getId(),pageable);
        }else{
            notificationPage = notificationRepository.findAllByConsumerMemberId(memberDto.getId(),pageable);
        }
        return notificationPage.map(NotificationDTO::new);

    }

    /**
     * 알람 읽음 상태 변경
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    @Transactional
    public void notificationCheck(Long notificationId, MemberDto memberDto) {
        Notification notification = findNotificationById(notificationId);
        authorityCheck(memberDto,notification);
        notification.setCheck(true);
        notificationRepository.save(notification);

    }

    /**
     * 알람 삭제
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    @Transactional
    public void deleteNotification(Long notificationId,MemberDto memberDto) {
        Notification notification = findNotificationById(notificationId);
        authorityCheck(memberDto,notification);
        notificationRepository.delete(notification);
    }


    /**
     * Notification 탐색
     * @param notificationId
     * @return Notification
     *
     * @author 이광석
     * @since 25.02.09
     */
    private Notification findNotificationById(Long notificationId){
        return notificationRepository.findById(notificationId)
                .orElseThrow(()-> new NotificationException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND.getStatus(),
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND.getErrorCode(),
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND.getMessage())
                );

    }


    /**
     * 로그인 된 사용자와 알림 member가 같은지 확인
     * @param memberDto
     * @param notification
     *
     * @author 이광석
     * @since 25.02.11
     */
    private void authorityCheck(MemberDto memberDto, Notification notification){

        if(!notification.getConsumerMemberId().equals(memberDto.getId())){
            throw new NotificationException(
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getStatus(),
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getMessage()
            );
        }
    }


    public Long getNotificationTotalCount(MemberDto memberDto) {
        Long memberId = memberDto.getId();

        return notificationRepository.countByConsumerMemberId(memberId);
    }
}
