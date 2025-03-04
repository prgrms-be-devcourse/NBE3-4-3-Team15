package com.project.backend.domain.challenge.attendance.repository;

import com.project.backend.domain.challenge.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * 챌린지 레포지토리
 *
 * @author 손진영
 * @since 25. 3. 04.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
