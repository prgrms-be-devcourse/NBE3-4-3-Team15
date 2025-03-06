package com.project.backend.domain.challenge.attendance.service;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import com.project.backend.domain.challenge.attendance.repository.AttendanceRepository;
import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.exception.ChallengeErrorCode;
import com.project.backend.domain.challenge.exception.ChallengeException;
import com.project.backend.domain.member.entity.Member;
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

    public boolean checkTodayAttendance(long challengeId, long memberId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);

        Optional<Attendance> op = attendanceRepository.findByChallengeIdAndMemberIdAndCreatedAtBetween(challengeId, memberId, startOfDay, endOfDay);

        return op.isPresent();
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void validateAttendance(Challenge challenge, Member member) {
        if (!checkTodayAttendance(challenge.getId(), member.getId())) {
            Optional<Attendance> opAttendance = Attendance.createAttendance(challenge, member);

            if (opAttendance.isEmpty()) {
                throw new ChallengeException(
                        ChallengeErrorCode.DAILY_VERIFICATION.getStatus(),
                        ChallengeErrorCode.DAILY_VERIFICATION.getErrorCode(),
                        ChallengeErrorCode.DAILY_VERIFICATION.getMessage()
                );
            }
            else {
                save(opAttendance.get());
            }
        }
    }
}
