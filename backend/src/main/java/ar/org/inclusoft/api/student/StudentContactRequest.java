package ar.org.inclusoft.api.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentContactRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Size(max = 80) String relationship,
        @NotBlank @Size(max = 30) String phoneNumber,
        @Email @Size(max = 120) String email,
        boolean responsible,
        boolean emergencyContact
) { }
