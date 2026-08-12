package ar.org.inclusoft.api.user;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import ar.org.inclusoft.api.staff.StaffMember;
import ar.org.inclusoft.api.staff.StaffMemberRepository;

@Service
@Transactional(readOnly = true)
class UserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffMemberRepository staffMemberRepository;

    UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, StaffMemberRepository staffMemberRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffMemberRepository = staffMemberRepository;
    }

    List<UserResponse> findAll() {
        return appUserRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(AppUser::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    UserResponse create(CreateUserRequest request) {
        String username = request.username().trim();
        if (appUserRepository.findByUsernameIgnoreCase(username).isPresent()) throw new DuplicateUsernameException(username);
        return UserResponse.from(appUserRepository.save(new AppUser(username, request.displayName().trim(), passwordEncoder.encode(request.password()), request.role(), resolveStaffMember(request.role(), request.staffMemberId(), null))));
    }

    @Transactional
    UserResponse update(String username, String actorUsername, UserRole actorRole, UpdateUserRequest request) {
        AppUser user = findByUsername(username);
        validateManagement(actorUsername, actorRole, user);
        if (username.equalsIgnoreCase(actorUsername) && user.getRole() != request.role()) throw new UserOperationNotAllowedException("No podés cambiar tu propio rol desde esta pantalla.");
        user.update(request.displayName().trim(), request.role(), resolveStaffMember(request.role(), request.staffMemberId(), user.getId()));
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            user.changePassword(passwordEncoder.encode(request.newPassword()));
            user.requirePasswordChange();
        }
        return UserResponse.from(user);
    }

    @Transactional
    UserResponse deactivate(String username, String actorUsername, UserRole actorRole) {
        AppUser user = findByUsername(username);
        validateManagement(actorUsername, actorRole, user);
        if (username.equalsIgnoreCase(actorUsername)) throw new UserOperationNotAllowedException("No podés deshabilitar tu propia cuenta.");
        user.deactivate();
        return UserResponse.from(user);
    }

    @Transactional
    UserResponse activate(String username, String actorUsername, UserRole actorRole) {
        AppUser user = findByUsername(username);
        validateManagement(actorUsername, actorRole, user);
        user.activate();
        return UserResponse.from(user);
    }

    private AppUser findByUsername(String username) { return appUserRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UserNotFoundException(username)); }
    private StaffMember resolveStaffMember(UserRole role, UUID staffMemberId, UUID userId) {
        if (role != UserRole.TEACHER) return null;
        if (staffMemberId == null) throw new UserOperationNotAllowedException("Para crear un acceso docente, seleccioná primero a la persona del equipo.");
        StaffMember member = staffMemberRepository.findById(staffMemberId).orElseThrow(() -> new UserOperationNotAllowedException("La persona seleccionada no existe."));
        if (!member.isActive()) throw new UserOperationNotAllowedException("No podés vincular una persona inactiva a un acceso docente.");
        boolean alreadyLinked = userId == null ? appUserRepository.existsByStaffMember_Id(staffMemberId) : appUserRepository.existsByStaffMember_IdAndIdNot(staffMemberId, userId);
        if (alreadyLinked) throw new UserOperationNotAllowedException("Esta persona ya está vinculada a otra cuenta de usuario.");
        return member;
    }
    private void validateManagement(String actorUsername, UserRole actorRole, AppUser user) {
        if (actorRole == UserRole.DIRECTOR && user.getRole() == UserRole.ADMIN) throw new UserOperationNotAllowedException("Solo un administrador puede gestionar una cuenta de administración.");
    }
}
