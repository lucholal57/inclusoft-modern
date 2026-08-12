package ar.org.inclusoft.api.student;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UpdateStudentRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 20) String documentNumber,
        @Size(max = 30) String phoneNumber,
        @Past LocalDate birthDate,
        @Size(max = 120) String birthPlace,
        @Size(max = 200) String address
) { }