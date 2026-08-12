package ar.org.inclusoft.api.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateUserRequest(
        @NotBlank @Size(max = 120) String displayName,
        @NotNull UserRole role,
        @Size(min = 8, max = 100) String newPassword,
        UUID staffMemberId
) { }
