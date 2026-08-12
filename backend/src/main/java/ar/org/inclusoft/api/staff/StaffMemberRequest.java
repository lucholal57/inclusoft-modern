package ar.org.inclusoft.api.staff;

import jakarta.validation.constraints.*;

public record StaffMemberRequest(@NotBlank @Size(max = 120) String fullName, @NotNull StaffProfile profile, @Size(max = 20) String documentNumber, @Size(max = 30) String phoneNumber, @Email @Size(max = 120) String email, @Size(max = 120) String profession) { }