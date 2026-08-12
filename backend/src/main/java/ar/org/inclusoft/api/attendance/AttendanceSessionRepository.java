package ar.org.inclusoft.api.attendance;
import java.time.LocalDate; import java.util.*; import org.springframework.data.jpa.repository.*;
interface AttendanceSessionRepository extends JpaRepository<AttendanceSession,UUID>{ @EntityGraph(attributePaths="entries") Optional<AttendanceSession> findByWorkshop_IdAndAttendanceDate(UUID workshopId,LocalDate date); }
