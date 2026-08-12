package ar.org.inclusoft.api.student;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentHealthRecordRepository extends JpaRepository<StudentHealthRecord, UUID> {
    Optional<StudentHealthRecord> findByStudent_Id(UUID studentId);
}
