package ar.org.inclusoft.api.student;

import jakarta.validation.constraints.Size;

public record StudentHealthRecordRequest(
        @Size(max = 1000) String medicalReferences,
        @Size(max = 1000) String medications,
        @Size(max = 1000) String allergies,
        @Size(max = 160) String healthInsurance,
        @Size(max = 1000) String treatingProfessionals,
        @Size(max = 2000) String supportGuidelines,
        @Size(max = 2000) String observations
) { }
