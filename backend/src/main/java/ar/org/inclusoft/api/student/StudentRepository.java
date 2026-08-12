package ar.org.inclusoft.api.student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByDocumentNumberAndIdNot(String documentNumber, UUID id);
    Optional<Student> findById(UUID id);
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCaseOrderByLastNameAscFirstNameAsc(String firstName, String lastName, String documentNumber);
    List<Student> findAllByOrderByLastNameAscFirstNameAsc();
}
