package ar.org.inclusoft.api.workshop;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import ar.org.inclusoft.api.staff.StaffMember;
import ar.org.inclusoft.api.staff.StaffMemberRepository;
import ar.org.inclusoft.api.student.Student;
import ar.org.inclusoft.api.student.StudentRepository;
import ar.org.inclusoft.api.user.AppUser;
import ar.org.inclusoft.api.user.AppUserRepository;
import ar.org.inclusoft.api.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class WorkshopService {
    private final WorkshopRepository workshopRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final StudentRepository studentRepository;
    private final AppUserRepository appUserRepository;

    WorkshopService(WorkshopRepository workshopRepository, StaffMemberRepository staffMemberRepository, StudentRepository studentRepository, AppUserRepository appUserRepository) {
        this.workshopRepository = workshopRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.studentRepository = studentRepository;
        this.appUserRepository = appUserRepository;
    }

    List<WorkshopResponse> findAccessible(String username, UserRole role, String search) {
        List<Workshop> workshops = search == null || search.isBlank() ? workshopRepository.findAllByOrderByNameAsc() : workshopRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByNameAsc(search.trim(), search.trim());
        return workshops.stream().filter(workshop -> search == null || search.isBlank() || workshop.getName().toLowerCase().contains(search.trim().toLowerCase())).map(WorkshopResponse::from).toList();
    }

    WorkshopDetailResponse findAccessibleById(String username, UserRole role, UUID id) {
        return WorkshopDetailResponse.from(findWorkshop(id));
    }

    List<WorkshopResponse> findAll(String search) {
        List<Workshop> workshops = search == null || search.isBlank()
                ? workshopRepository.findAllByOrderByNameAsc()
                : workshopRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByNameAsc(search.trim(), search.trim());
        return workshops.stream().map(WorkshopResponse::from).toList();
    }

    WorkshopDetailResponse findById(UUID id) {
        return WorkshopDetailResponse.from(workshopRepository.findById(id).orElseThrow(() -> new WorkshopNotFoundException(id)));
    }

    @Transactional
    WorkshopResponse create(CreateWorkshopRequest request) {
        String name = request.name().trim();
        if (workshopRepository.existsByNameIgnoreCase(name)) throw new DuplicateWorkshopNameException(name);
        Set<StaffMember> staffMembers = resolveStaffMembers(request.staffMemberIds());
        Set<Student> students = resolveStudents(request.studentIds());
        if (students.size() > request.capacity()) throw new InvalidWorkshopConfigurationException("La cantidad de alumnos no puede superar el cupo del taller.");
        Set<WorkshopSchedule> schedules = createSchedules(request.schedules());
        validateScheduleAvailability(staffMembers, students, schedules, null);
        Workshop workshop = new Workshop(name, trimToNull(request.description()), request.capacity());
        workshop.assignStaffMembers(staffMembers);
        workshop.enrollStudents(students);
        workshop.defineSchedules(schedules);
        return WorkshopResponse.from(workshopRepository.save(workshop));
    }


    @Transactional
    WorkshopResponse update(UUID id, UpdateWorkshopRequest request) {
        Workshop workshop = findWorkshop(id);
        String name = request.name().trim();
        if (workshopRepository.existsByNameIgnoreCaseAndIdNot(name, id)) throw new DuplicateWorkshopNameException(name);
        if (workshop.getStudents().size() > request.capacity()) throw new InvalidWorkshopConfigurationException("El cupo no puede ser menor a la cantidad de alumnos ya asignados.");
        Set<WorkshopSchedule> schedules = createSchedules(request.schedules());
        validateScheduleAvailability(workshop.getStaffMembers(), workshop.getStudents(), schedules, id);
        workshop.update(name, trimToNull(request.description()), request.capacity());
        workshop.defineSchedules(schedules);
        return WorkshopResponse.from(workshop);
    }

    @Transactional
    WorkshopDetailResponse replaceStudents(UUID id, UpdateWorkshopStudentsRequest request) {
        Workshop workshop = findWorkshop(id);
        Set<Student> students = resolveStudents(request.studentIds());
        if (students.size() > workshop.getCapacity()) throw new InvalidWorkshopConfigurationException("La cantidad de alumnos no puede superar el cupo del taller.");
        validateScheduleAvailability(workshop.getStaffMembers(), students, workshop.getSchedules(), id);
        workshop.enrollStudents(students);
        return WorkshopDetailResponse.from(workshop);
    }

    @Transactional
    WorkshopDetailResponse replaceTeam(UUID id, UpdateWorkshopTeamRequest request) {
        Workshop workshop = findWorkshop(id);
        Set<StaffMember> staffMembers = resolveStaffMembers(request.staffMemberIds());
        validateScheduleAvailability(staffMembers, workshop.getStudents(), workshop.getSchedules(), id);
        workshop.assignStaffMembers(staffMembers);
        return WorkshopDetailResponse.from(workshop);
    }
    @Transactional
    WorkshopResponse deactivate(UUID id) { Workshop workshop = findWorkshop(id); workshop.deactivate(); return WorkshopResponse.from(workshop); }

    @Transactional
    WorkshopResponse activate(UUID id) { Workshop workshop = findWorkshop(id); workshop.activate(); return WorkshopResponse.from(workshop); }

    private Workshop findWorkshop(UUID id) { return workshopRepository.findById(id).orElseThrow(() -> new WorkshopNotFoundException(id)); }
    private List<Workshop> workshopsForTeacher(String username) { AppUser user = appUserRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new WorkshopNotFoundException()); if (user.getStaffMember() == null) return List.of(); return workshopRepository.findDistinctByStaffMembers_Id(user.getStaffMember().getId()); }
    private Set<StaffMember> resolveStaffMembers(List<UUID> ids) {
        Set<UUID> requestedIds = new LinkedHashSet<>(safeList(ids));
        List<StaffMember> members = staffMemberRepository.findAllById(requestedIds);
        if (members.size() != requestedIds.size()) throw new InvalidWorkshopConfigurationException("Uno o más integrantes del equipo no existen.");
        if (members.stream().anyMatch(member -> !member.isActive())) throw new InvalidWorkshopConfigurationException("No se puede asignar una persona inactiva al taller.");
        return new LinkedHashSet<>(members);
    }

    private Set<Student> resolveStudents(List<UUID> ids) {
        Set<UUID> requestedIds = new LinkedHashSet<>(safeList(ids));
        List<Student> students = studentRepository.findAllById(requestedIds);
        if (students.size() != requestedIds.size()) throw new InvalidWorkshopConfigurationException("Uno o más alumnos no existen.");
        return new LinkedHashSet<>(students);
    }

    private Set<WorkshopSchedule> createSchedules(List<WorkshopScheduleRequest> requests) {
        Set<WorkshopSchedule> schedules = new LinkedHashSet<>();
        for (WorkshopScheduleRequest request : safeList(requests)) {
            if (!request.endTime().isAfter(request.startTime())) throw new InvalidWorkshopConfigurationException("La hora de finalización debe ser posterior a la de inicio.");
            schedules.add(new WorkshopSchedule(request.dayOfWeek(), request.startTime(), request.endTime(), trimToNull(request.location())));
        }
        return schedules;
    }


    private void validateScheduleAvailability(Set<StaffMember> staffMembers, Set<Student> students, Set<WorkshopSchedule> schedules, UUID workshopBeingEdited) {
        List<WorkshopSchedule> proposedSchedules = List.copyOf(schedules);
        for (int first = 0; first < proposedSchedules.size(); first++) {
            for (int second = first + 1; second < proposedSchedules.size(); second++) {
                if (overlaps(proposedSchedules.get(first), proposedSchedules.get(second))) {
                    throw new InvalidWorkshopConfigurationException("Un mismo taller no puede tener horarios superpuestos.");
                }
            }
        }
        for (StaffMember member : staffMembers) {
            ensureNoConflict("La persona del equipo " + member.getFullName(), workshopRepository.findDistinctByStaffMembers_Id(member.getId()), proposedSchedules, workshopBeingEdited);
        }
        for (Student student : students) {
            ensureNoConflict("El alumno " + student.getLastName() + ", " + student.getFirstName(), workshopRepository.findDistinctByStudents_Id(student.getId()), proposedSchedules, workshopBeingEdited);
        }
    }

    private void ensureNoConflict(String personDescription, List<Workshop> assignedWorkshops, List<WorkshopSchedule> proposedSchedules, UUID workshopBeingEdited) {
        for (Workshop assignedWorkshop : assignedWorkshops) {
            if (assignedWorkshop.getId().equals(workshopBeingEdited)) continue;
            for (WorkshopSchedule assignedSchedule : assignedWorkshop.getSchedules()) {
                for (WorkshopSchedule proposedSchedule : proposedSchedules) {
                    if (overlaps(assignedSchedule, proposedSchedule)) {
                        throw new InvalidWorkshopConfigurationException(personDescription + " ya tiene un cruce con el taller '" + assignedWorkshop.getName() + "' en ese día y horario.");
                    }
                }
            }
        }
    }

    private boolean overlaps(WorkshopSchedule first, WorkshopSchedule second) {
        return first.getDayOfWeek() == second.getDayOfWeek()
                && first.getStartTime().isBefore(second.getEndTime())
                && second.getStartTime().isBefore(first.getEndTime());
    }
    private <T> List<T> safeList(List<T> values) { return values == null ? List.of() : values; }
    private String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}






