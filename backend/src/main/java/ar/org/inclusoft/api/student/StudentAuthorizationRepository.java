package ar.org.inclusoft.api.student;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentAuthorizationRepository extends JpaRepository<StudentAuthorization, UUID> {
    Optional<StudentAuthorization> findByStudent_Id(UUID studentId);
}
