package ar.org.inclusoft.api.student;

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
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    StudentController(StudentService studentService) { this.studentService = studentService; }
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public List<StudentResponse> findAll(@RequestParam(required = false) String search) { return studentService.findAll(search); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public StudentResponse findById(@PathVariable UUID id) { return studentService.findById(id); }
    @GetMapping("/{id}/contacts") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public List<StudentContactResponse> findContacts(@PathVariable UUID id) { return studentService.findContacts(id); }
    @GetMapping("/{id}/health") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public StudentHealthRecordResponse findHealthRecord(@PathVariable UUID id) { return studentService.findHealthRecord(id); }
    @GetMapping("/{id}/authorizations") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR', 'TEACHER')") public StudentAuthorizationResponse findAuthorizations(@PathVariable UUID id) { return studentService.findAuthorizations(id); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) { StudentResponse student = studentService.create(request); return ResponseEntity.created(URI.create("/api/v1/students/" + student.id())).body(student); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public StudentResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateStudentRequest request) { return studentService.update(id, request); }
    @PutMapping("/{id}/contacts") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public List<StudentContactResponse> replaceContacts(@PathVariable UUID id, @Valid @RequestBody List<@Valid StudentContactRequest> request) { return studentService.replaceContacts(id, request); }
    @PutMapping("/{id}/health") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public StudentHealthRecordResponse replaceHealthRecord(@PathVariable UUID id, @Valid @RequestBody StudentHealthRecordRequest request) { return studentService.replaceHealthRecord(id, request); }
    @PutMapping("/{id}/authorizations") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public StudentAuthorizationResponse replaceAuthorizations(@PathVariable UUID id, @Valid @RequestBody StudentAuthorizationRequest request) { return studentService.replaceAuthorizations(id, request); }
    @PatchMapping("/{id}/deactivate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public StudentResponse deactivate(@PathVariable UUID id) { return studentService.deactivate(id); }
    @PatchMapping("/{id}/activate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')") public StudentResponse activate(@PathVariable UUID id) { return studentService.activate(id); }
    @ExceptionHandler(DuplicateDocumentNumberException.class) ResponseEntity<ProblemResponse> handleDuplicateDocument(DuplicateDocumentNumberException exception) { return ResponseEntity.status(HttpStatus.CONFLICT).body(new ProblemResponse(exception.getMessage())); }
    @ExceptionHandler(StudentNotFoundException.class) ResponseEntity<ProblemResponse> handleStudentNotFound(StudentNotFoundException exception) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProblemResponse(exception.getMessage())); }
    record ProblemResponse(String message) { }
}
