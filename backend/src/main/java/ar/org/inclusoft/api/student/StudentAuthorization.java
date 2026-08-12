package ar.org.inclusoft.api.student;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "student_authorizations")
class StudentAuthorization {
    @Id @UuidGenerator private UUID id;
    @OneToOne(optional = false) @JoinColumn(name = "student_id", nullable = false, unique = true) private Student student;
    @Column(name = "image_use_authorized", nullable = false) private boolean imageUseAuthorized;
    @Column(name = "local_outings_authorized", nullable = false) private boolean localOutingsAuthorized;
    @Column(name = "medical_emergency_authorized", nullable = false) private boolean medicalEmergencyAuthorized;
    @Column(name = "data_sharing_authorized", nullable = false) private boolean dataSharingAuthorized;
    @Column(name = "authorized_by", length = 120) private String authorizedBy;
    @Column(name = "authorization_date") private LocalDate authorizationDate;
    @Column(length = 2000) private String observations;

    protected StudentAuthorization() { }
    StudentAuthorization(Student student) { this.student = student; }
    void update(StudentAuthorizationRequest request) { imageUseAuthorized = request.imageUseAuthorized(); localOutingsAuthorized = request.localOutingsAuthorized(); medicalEmergencyAuthorized = request.medicalEmergencyAuthorized(); dataSharingAuthorized = request.dataSharingAuthorized(); authorizedBy = request.authorizedBy(); authorizationDate = request.authorizationDate(); observations = request.observations(); }
    boolean isImageUseAuthorized() { return imageUseAuthorized; }
    boolean isLocalOutingsAuthorized() { return localOutingsAuthorized; }
    boolean isMedicalEmergencyAuthorized() { return medicalEmergencyAuthorized; }
    boolean isDataSharingAuthorized() { return dataSharingAuthorized; }
    String getAuthorizedBy() { return authorizedBy; }
    LocalDate getAuthorizationDate() { return authorizationDate; }
    String getObservations() { return observations; }
}
