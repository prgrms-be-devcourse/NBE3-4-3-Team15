package com.project.backend;

import com.project.backend.domain.notification.entity.Notification;
import com.project.backend.domain.notification.repository.NotificationRepository;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.domain.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private NotificationService notificationService;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification_Success() throws IOException {
        // Given
        Long testMemberId = 1L;
        String testMessage = "테스트 알림 메시지";

        // SSE Emitter 생성 후 저장
        SseEmitter emitter = mock(SseEmitter.class);
        emitters.put(testMemberId, emitter);

        // When
        notificationService.sendNotification(testMemberId, testMessage);

        // Then
        verify(emitter, times(1)).send(SseEmitter.event().name("notification").data(testMessage));
    }

    @Test
    void sendNotification_Failure() {
        // Given
        Long testMemberId = 2L;
        String testMessage = "실패 테스트 메시지";

        // Emitter를 저장하지 않아 알림 전송이 실패해야 함
        // When
        notificationService.sendNotification(testMemberId, testMessage);

        // Then
        // 로그 메시지 "알람 전달 실패"가 출력되어야 함 (콘솔에서 확인 가능)
    }
}
