package com.project.backend.domain.notification.service;


import com.project.backend.domain.follow.dto.FollowResponseDto;
import com.project.backend.domain.follow.entity.Follow;
import com.project.backend.domain.follow.service.FollowService;
import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.exception.NotificationErrorCode;
import com.project.backend.domain.notification.exception.NotificationException;
import com.project.backend.domain.notification.repository.EmitterRepository;
import com.project.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 알람 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;
    private final FollowService followService;
    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 600L *1000*60;

    /**
     * SSE 연결 메소드
     * @param username
     * @return emitter
     *
     * @author 이광석
     * @since 25.02.23
     */
    public SseEmitter subscribe(String username){
        Long memberId = memberService.getMyProfile(username).getId();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(memberId,emitter);

        emitter.onCompletion(()->emitterRepository.deleteBy(memberId));
        emitter.onTimeout(()->emitterRepository.deleteBy(memberId));

        try{
           emitter.send(SseEmitter.event()
                   .name("connect")
                   .data("SSE 연결 성공"));
        }catch (IOException e){
           emitterRepository.deleteBy(memberId);
           emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 프론트로 알람 전달 메소드
     * @param memberId
     * @param message
     *
     * @author 이광석
     * @since 25.02.23
     */
    public void sendNotification(Long memberId,String message){
        SseEmitter emitter = emitterRepository.findById(memberId);
        if(emitter !=null){
            try{
                System.out.println("알람 전달 성공");
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            }catch(IOException e){
                System.out.println("알람 전달 실패");
                emitterRepository.deleteBy(memberId);
                emitter.completeWithError(e);
            }
        }
    }



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

        sendNotification(notification.getMemberId(),notification.getContent());
        return new NotificationDTO(notificationRepository.save(notification));
    }

    /**
     * 알람 조회
     * @param memberId
     * @return List<NotificationDTO>
     *
     * @author 이광석
     * @since 25.02.06
     */
    public List<NotificationDTO> findByUser(Long memberId) {
        return notificationRepository.findALLByMemberId(memberId);
    }

    /**
     * 알람 읽음 상태 변경
     * @param notificationId
     *
     * @author 이광석
     * @since 25.02.06
     */
    public void notificationCheck(Long notificationId) {
        Notification notification = findNotificationById(notificationId);
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
    public void notificationDelete(Long notificationId) {
        Notification notification = findNotificationById(notificationId);
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
