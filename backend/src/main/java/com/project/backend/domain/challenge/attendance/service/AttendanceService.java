package com.project.backend.domain.challenge.attendance.service;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import com.project.backend.domain.challenge.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * 출석 서비스
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public boolean getCheck(long challengeId, long memberId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        Optional<Attendance> op = attendanceRepository.findByChallengeIdAndMemberIdAndCreatedAtBetween(challengeId, memberId, startOfDay, endOfDay);

        return op.isPresent();
    }
}
