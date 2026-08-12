package ar.org.inclusoft.api.staff;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {
    List<StaffMember> findAllByOrderByFullNameAsc();
    List<StaffMember> findByFullNameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCaseOrProfessionContainingIgnoreCaseOrderByFullNameAsc(String fullName, String documentNumber, String profession);
    Optional<StaffMember> findByDocumentNumber(String documentNumber);
    boolean existsByDocumentNumberAndIdNot(String documentNumber, UUID id);
}
