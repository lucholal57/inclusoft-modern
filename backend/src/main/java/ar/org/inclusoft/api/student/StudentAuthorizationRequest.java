package ar.org.inclusoft.api.student;

import java.time.LocalDate;
import jakarta.validation.constraints.Size;

public record StudentAuthorizationRequest(
        boolean imageUseAuthorized,
        boolean localOutingsAuthorized,
        boolean medicalEmergencyAuthorized,
        boolean dataSharingAuthorized,
        @Size(max = 120) String authorizedBy,
        LocalDate authorizationDate,
        @Size(max = 2000) String observations
) { }
