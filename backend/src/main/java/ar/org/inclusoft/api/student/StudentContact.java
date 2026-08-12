package ar.org.inclusoft.api.student;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "student_contacts")
class StudentContact {
    @Id @UuidGenerator private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id", nullable = false) private Student student;
    @Column(name = "full_name", nullable = false, length = 120) private String fullName;
    @Column(nullable = false, length = 80) private String relationship;
    @Column(name = "phone_number", nullable = false, length = 30) private String phoneNumber;
    @Column(length = 120) private String email;
    @Column(nullable = false) private boolean responsible;
    @Column(name = "emergency_contact", nullable = false) private boolean emergencyContact;

    protected StudentContact() { }
    StudentContact(Student student, String fullName, String relationship, String phoneNumber, String email, boolean responsible, boolean emergencyContact) {
        this.student = student; this.fullName = fullName; this.relationship = relationship; this.phoneNumber = phoneNumber; this.email = email; this.responsible = responsible; this.emergencyContact = emergencyContact;
    }
    UUID getId() { return id; }
    String getFullName() { return fullName; }
    String getRelationship() { return relationship; }
    String getPhoneNumber() { return phoneNumber; }
    String getEmail() { return email; }
    boolean isResponsible() { return responsible; }
    boolean isEmergencyContact() { return emergencyContact; }
}
