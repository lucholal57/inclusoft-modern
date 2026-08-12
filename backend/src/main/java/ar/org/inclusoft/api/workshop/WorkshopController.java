package ar.org.inclusoft.api.workshop;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import ar.org.inclusoft.api.user.UserRole;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workshops")
public class WorkshopController {
    private final WorkshopService workshopService;
    WorkshopController(WorkshopService workshopService) { this.workshopService = workshopService; }
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public List<WorkshopResponse> findAll(@RequestParam(required = false) String search, Authentication authentication) { return workshopService.findAccessible(authentication.getName(), roleOf(authentication), search); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public WorkshopDetailResponse findById(@PathVariable UUID id, Authentication authentication) { return workshopService.findAccessibleById(authentication.getName(), roleOf(authentication), id); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public ResponseEntity<WorkshopResponse> create(@Valid @RequestBody CreateWorkshopRequest request) { WorkshopResponse workshop = workshopService.create(request); return ResponseEntity.created(URI.create("/api/v1/workshops/" + workshop.id())).body(workshop); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public WorkshopResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateWorkshopRequest request) { return workshopService.update(id, request); }
    @PutMapping("/{id}/students") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public WorkshopDetailResponse replaceStudents(@PathVariable UUID id, @RequestBody UpdateWorkshopStudentsRequest request) { return workshopService.replaceStudents(id, request); }
    @PutMapping("/{id}/team") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public WorkshopDetailResponse replaceTeam(@PathVariable UUID id, @RequestBody UpdateWorkshopTeamRequest request) { return workshopService.replaceTeam(id, request); }
    @PatchMapping("/{id}/deactivate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public WorkshopResponse deactivate(@PathVariable UUID id) { return workshopService.deactivate(id); }
    @PatchMapping("/{id}/activate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public WorkshopResponse activate(@PathVariable UUID id) { return workshopService.activate(id); }
    private UserRole roleOf(Authentication authentication) { return authentication.getAuthorities().stream().map(authority -> authority.getAuthority()).filter(authority -> authority.startsWith("ROLE_")).findFirst().map(authority -> UserRole.valueOf(authority.substring(5))).orElseThrow(); }
    @ExceptionHandler({DuplicateWorkshopNameException.class, InvalidWorkshopConfigurationException.class}) ResponseEntity<ProblemResponse> handleInvalidConfiguration(RuntimeException exception) { return ResponseEntity.status(HttpStatus.CONFLICT).body(new ProblemResponse(exception.getMessage())); }
    @ExceptionHandler(WorkshopNotFoundException.class) ResponseEntity<ProblemResponse> handleNotFound(WorkshopNotFoundException exception) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProblemResponse(exception.getMessage())); }
    record ProblemResponse(String message) { }
}

