package ar.org.inclusoft.api.student;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "student_health_records")
class StudentHealthRecord {
    @Id @UuidGenerator private UUID id;
    @OneToOne(optional = false) @JoinColumn(name = "student_id", nullable = false, unique = true) private Student student;
    @Column(name = "medical_references", length = 1000) private String medicalReferences;
    @Column(length = 1000) private String medications;
    @Column(length = 1000) private String allergies;
    @Column(name = "health_insurance", length = 160) private String healthInsurance;
    @Column(name = "treating_professionals", length = 1000) private String treatingProfessionals;
    @Column(name = "support_guidelines", length = 2000) private String supportGuidelines;
    @Column(length = 2000) private String observations;

    protected StudentHealthRecord() { }
    StudentHealthRecord(Student student) { this.student = student; }
    void update(StudentHealthRecordRequest request) {
        medicalReferences = request.medicalReferences(); medications = request.medications(); allergies = request.allergies(); healthInsurance = request.healthInsurance(); treatingProfessionals = request.treatingProfessionals(); supportGuidelines = request.supportGuidelines(); observations = request.observations();
    }
    String getMedicalReferences() { return medicalReferences; }
    String getMedications() { return medications; }
    String getAllergies() { return allergies; }
    String getHealthInsurance() { return healthInsurance; }
    String getTreatingProfessionals() { return treatingProfessionals; }
    String getSupportGuidelines() { return supportGuidelines; }
    String getObservations() { return observations; }
}
