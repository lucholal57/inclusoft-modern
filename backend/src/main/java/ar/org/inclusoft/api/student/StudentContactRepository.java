package ar.org.inclusoft.api.student;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentContactRepository extends JpaRepository<StudentContact, UUID> {
    List<StudentContact> findByStudent_IdOrderByFullNameAsc(UUID studentId);
    void deleteByStudent_Id(UUID studentId);
}
