package ar.org.inclusoft.api.student;

import java.time.LocalDate;

public record StudentAuthorizationResponse(boolean imageUseAuthorized, boolean localOutingsAuthorized, boolean medicalEmergencyAuthorized, boolean dataSharingAuthorized, String authorizedBy, LocalDate authorizationDate, String observations) {
    static StudentAuthorizationResponse from(StudentAuthorization authorization) { return new StudentAuthorizationResponse(authorization.isImageUseAuthorized(), authorization.isLocalOutingsAuthorized(), authorization.isMedicalEmergencyAuthorized(), authorization.isDataSharingAuthorized(), authorization.getAuthorizedBy(), authorization.getAuthorizationDate(), authorization.getObservations()); }
    static StudentAuthorizationResponse empty() { return new StudentAuthorizationResponse(false, false, false, false, null, null, null); }
}
