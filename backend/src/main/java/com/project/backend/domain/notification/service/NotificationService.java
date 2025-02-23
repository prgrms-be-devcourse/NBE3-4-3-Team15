package com.project.backend.domain.notification.service;


import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.dto.NotificationDTO;
import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.exception.NotificationErrorCode;
import com.project.backend.domain.notification.exception.NotificationException;
import com.project.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 알람 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    private static final Long DEFAULT_TIMEOUT = 600L *1000*60;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 연결 메소드
     * @param username
     * @return emitter
     *
     * @author 이광석
     * @since 25.02.23
     */
    public SseEmitter subscribe(String username){
        Long userId = memberService.getMyProfile(username).getId();
       SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

       emitter.onCompletion(()->emitters.remove(userId));
       emitter.onTimeout(()->emitters.remove(userId));

       try{
           emitter.send(SseEmitter.event()
                   .name("connect")
                   .data("SSE 연결 성공"));
       }catch (IOException e){
           emitters.remove(userId);
           emitter.completeWithError(e);
       }

       return emitter;
    }

    public void sendNotification(String username,String message){
        Long userId = memberService.getMyProfile(username).getId();
        SseEmitter emitter = emitters.get(userId);
        if(emitter !=null){
            try{
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            }catch(IOException e){
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
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
                .memberId(notificationDTO.getMemberId())
                .reviewId(notificationDTO.getReviewId())
                .reviewCommentId(notificationDTO.getReviewComment())
                .isCheck(notificationDTO.isCheck())
                .content(notificationDTO.getContent())
                .build();
        Notification saveNotification  = notificationRepository.save(notification);

        MemberDto memberDto = memberService.getMemberById(saveNotification.getMemberId());


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


}
