package ar.org.inclusoft.api.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsernameIgnoreCase(String username);
    boolean existsByStaffMember_IdAndIdNot(UUID staffMemberId, UUID id);
    boolean existsByStaffMember_Id(UUID staffMemberId);
}
