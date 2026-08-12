package ar.org.inclusoft.api.workshop;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, UUID> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    List<Workshop> findAllByOrderByNameAsc();
    List<Workshop> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByNameAsc(String name, String description);
    @EntityGraph(attributePaths = "schedules")
    List<Workshop> findDistinctByStaffMembers_Id(UUID staffMemberId);
    @EntityGraph(attributePaths = "schedules")
    List<Workshop> findDistinctByStudents_Id(UUID studentId);
}

