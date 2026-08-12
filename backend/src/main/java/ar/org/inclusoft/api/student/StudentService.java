package ar.org.inclusoft.api.student;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.org.inclusoft.api.user.AppUser;
import ar.org.inclusoft.api.user.AppUserRepository;
import ar.org.inclusoft.api.user.UserRole;
import ar.org.inclusoft.api.workshop.WorkshopRepository;

@Service
@Transactional(readOnly = true)
class StudentService {
    private final StudentRepository studentRepository;
    private final StudentContactRepository studentContactRepository;
    private final StudentHealthRecordRepository studentHealthRecordRepository;
    private final StudentAuthorizationRepository studentAuthorizationRepository;
    private final AppUserRepository appUserRepository;
    private final WorkshopRepository workshopRepository;
    StudentService(StudentRepository studentRepository, StudentContactRepository studentContactRepository, StudentHealthRecordRepository studentHealthRecordRepository, StudentAuthorizationRepository studentAuthorizationRepository, AppUserRepository appUserRepository, WorkshopRepository workshopRepository) { this.studentRepository = studentRepository; this.studentContactRepository = studentContactRepository; this.studentHealthRecordRepository = studentHealthRecordRepository; this.studentAuthorizationRepository = studentAuthorizationRepository; this.appUserRepository = appUserRepository; this.workshopRepository = workshopRepository; }
    List<StudentResponse> findAll(String search) {
        List<Student> students = search == null || search.isBlank() ? studentRepository.findAllByOrderByLastNameAscFirstNameAsc() : studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCaseOrderByLastNameAscFirstNameAsc(search, search, search);
        return students.stream().map(StudentResponse::from).toList();
    }
    List<StudentResponse> findAccessible(String username, UserRole role, String search) { if (role != UserRole.TEACHER) return findAll(search); AppUser user = appUserRepository.findByUsernameIgnoreCase(username).orElseThrow(StudentNotFoundException::new); if (user.getStaffMember() == null) return List.of(); return workshopRepository.findDistinctByStaffMembers_Id(user.getStaffMember().getId()).stream().flatMap(workshop -> workshop.getStudents().stream()).distinct().filter(student -> search == null || search.isBlank() || (student.getFirstName() + " " + student.getLastName() + " " + student.getDocumentNumber()).toLowerCase().contains(search.trim().toLowerCase())).sorted(java.util.Comparator.comparing(Student::getLastName).thenComparing(Student::getFirstName)).map(StudentResponse::from).toList(); }
    StudentResponse findById(UUID id) { return StudentResponse.from(findStudent(id)); }
    List<StudentContactResponse> findContacts(UUID id) { findStudent(id); return studentContactRepository.findByStudent_IdOrderByFullNameAsc(id).stream().map(StudentContactResponse::from).toList(); }
    StudentHealthRecordResponse findHealthRecord(UUID id) { findStudent(id); return studentHealthRecordRepository.findByStudent_Id(id).map(StudentHealthRecordResponse::from).orElseGet(StudentHealthRecordResponse::empty); }
    StudentAuthorizationResponse findAuthorizations(UUID id) { findStudent(id); return studentAuthorizationRepository.findByStudent_Id(id).map(StudentAuthorizationResponse::from).orElseGet(StudentAuthorizationResponse::empty); }
    @Transactional StudentResponse create(CreateStudentRequest request) {
        if (studentRepository.existsByDocumentNumber(request.documentNumber().trim())) throw new DuplicateDocumentNumberException(request.documentNumber());
        return StudentResponse.from(studentRepository.save(new Student(request.firstName().trim(), request.lastName().trim(), request.documentNumber().trim(), trimToNull(request.phoneNumber()), request.birthDate(), trimToNull(request.birthPlace()), trimToNull(request.address()))));
    }
    @Transactional StudentResponse update(UUID id, UpdateStudentRequest request) {
        Student student = findStudent(id);
        String documentNumber = request.documentNumber().trim();
        if (studentRepository.existsByDocumentNumberAndIdNot(documentNumber, id)) throw new DuplicateDocumentNumberException(documentNumber);
        student.update(request.firstName().trim(), request.lastName().trim(), documentNumber, trimToNull(request.phoneNumber()), request.birthDate(), trimToNull(request.birthPlace()), trimToNull(request.address()));
        return StudentResponse.from(student);
    }
    @Transactional StudentResponse deactivate(UUID id) { Student student = findStudent(id); student.deactivate(); return StudentResponse.from(student); }
    @Transactional StudentResponse activate(UUID id) { Student student = findStudent(id); student.activate(); return StudentResponse.from(student); }
    @Transactional List<StudentContactResponse> replaceContacts(UUID id, List<@Valid StudentContactRequest> requests) {
        Student student = findStudent(id);
        List<StudentContactRequest> contacts = requests == null ? List.of() : requests;
        studentContactRepository.deleteByStudent_Id(id);
        List<StudentContact> saved = studentContactRepository.saveAll(contacts.stream().map(request -> new StudentContact(student, request.fullName().trim(), request.relationship().trim(), request.phoneNumber().trim(), trimToNull(request.email()), request.responsible(), request.emergencyContact())).toList());
        return saved.stream().map(StudentContactResponse::from).toList();
    }
    @Transactional StudentHealthRecordResponse replaceHealthRecord(UUID id, StudentHealthRecordRequest request) {
        Student student = findStudent(id);
        StudentHealthRecord record = studentHealthRecordRepository.findByStudent_Id(id).orElseGet(() -> new StudentHealthRecord(student));
        record.update(new StudentHealthRecordRequest(trimToNull(request.medicalReferences()), trimToNull(request.medications()), trimToNull(request.allergies()), trimToNull(request.healthInsurance()), trimToNull(request.treatingProfessionals()), trimToNull(request.supportGuidelines()), trimToNull(request.observations())));
        return StudentHealthRecordResponse.from(studentHealthRecordRepository.save(record));
    }
    @Transactional StudentAuthorizationResponse replaceAuthorizations(UUID id, StudentAuthorizationRequest request) {
        Student student = findStudent(id);
        StudentAuthorization authorization = studentAuthorizationRepository.findByStudent_Id(id).orElseGet(() -> new StudentAuthorization(student));
        authorization.update(new StudentAuthorizationRequest(request.imageUseAuthorized(), request.localOutingsAuthorized(), request.medicalEmergencyAuthorized(), request.dataSharingAuthorized(), trimToNull(request.authorizedBy()), request.authorizationDate(), trimToNull(request.observations())));
        return StudentAuthorizationResponse.from(studentAuthorizationRepository.save(authorization));
    }
    private Student findStudent(UUID id) { return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id)); }
    private String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
