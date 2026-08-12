package ar.org.inclusoft.api.student;

import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String firstName,
        String lastName,
        String documentNumber,
        String phoneNumber,
        LocalDate birthDate,
        String birthPlace,
        String address,
        StudentStatus status
) {
    static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(), student.getFirstName(), student.getLastName(), student.getDocumentNumber(),
                student.getPhoneNumber(), student.getBirthDate(), student.getBirthPlace(), student.getAddress(), student.getStatus()
        );
    }
}