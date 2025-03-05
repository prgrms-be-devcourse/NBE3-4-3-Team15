package com.project.backend.domain.notification.service;



import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.exception.NotificationErrorCode;
import com.project.backend.domain.notification.exception.NotificationException;
import com.project.backend.global.rabbitmq.dto.MessageDto;
import com.project.backend.global.rabbitmq.service.RabbitMQService;
import com.project.backend.domain.notification.repository.NotificationRepository;
import com.project.backend.global.authority.CustomUserDetails;

import com.project.backend.global.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 알람 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    private final SseService sseservice;
    private final RabbitMQService rabbitMQService;

    private static final Long DEFAULT_TIMEOUT = 600L *1000*60;



    /**
     * 알람 생성
     * 팔로워들에게 알림 전달(내가 리뷰 작성시)
     * 댓글 작성시 리뷰 작성자에게
     * 대댓글 작성시 댓글 작성자에게
     *
     *
     * @param notificationDTO
     * @return NotificationDTO
     *
     * @author 이광석
     * @since 25.02.06
     */
    public NotificationDTO create(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .memberId(notificationDTO.getMemberId())
                .reviewId(notificationDTO.getReviewId())
                .reviewCommentId(notificationDTO.getReviewComment())
                .isCheck(notificationDTO.isCheck())
                .content(notificationDTO.getContent())
                .build();


        MessageDto newMessage = new MessageDto(notification.getMemberId(),notification.getContent()); //producer에 전달한 message 생성


        rabbitMQService.sendMessage(notification.getMemberId(),newMessage);//producer

        return new NotificationDTO(notificationRepository.save(notification));
    }

    /**
     * 알람 조회
     * @param userDetails
     * @return List<NotificationDTO>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public List<NotificationDTO> findByUser(CustomUserDetails userDetails) {
        MemberDto member = memberService.getMyProfile(userDetails.getUsername());
        return notificationRepository.findALLByMemberId(member.getId());
    }

    /**
     * 알람 읽음 상태 변경
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    public void notificationCheck(Long notificationId, CustomUserDetails userDetails) {
        Notification notification = findNotificationById(notificationId);
        authorityCheck(userDetails,notification);
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
    public void notificationDelete(Long notificationId,CustomUserDetails userDetails) {
        Notification notification = findNotificationById(notificationId);
        authorityCheck(userDetails,notification);
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
     * @param userDetails
     * @param notification
     *
     * @author 이광석
     * @since 25.02.11
     */
    private void authorityCheck(CustomUserDetails userDetails, Notification notification){
        MemberDto memberDto = memberService.getMyProfile(userDetails.getUsername());

        if(notification.getMemberId()!=memberDto.getId()){
            throw new NotificationException(
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getStatus(),
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),
                    NotificationErrorCode.UNAUTHORIZED_ACCESS.getMessage()
            );
        }
    }
    /**
     * member기반 알림 리스트 출력
     * @param username
     * @return List<NotificationDTO>
     *
     * @author 이광석
     * @since 25.02.23
     */
    public List<NotificationDTO> getMyNotification(String username) {
        Long userId = memberService.getMyProfile(username).getId();
        List<Notification> notifications = notificationRepository.findAllByMemberId(userId);
        List<NotificationDTO> notificationDTOS = notifications.stream()
                .map(notification -> new NotificationDTO(notification))
                .collect(Collectors.toList());
        return notificationDTOS;
    }



}
