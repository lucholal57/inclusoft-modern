package ar.org.inclusoft.api.student;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "students")
public class Student {
    @Id @UuidGenerator private UUID id;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(name = "document_number", nullable = false, unique = true, length = 20) private String documentNumber;
    @Column(name = "phone_number", length = 30) private String phoneNumber;
    @Column(name = "birth_date") private LocalDate birthDate;
    @Column(name = "birth_place", length = 120) private String birthPlace;
    @Column(name = "address", length = 200) private String address;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StudentStatus status = StudentStatus.ACTIVE;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    protected Student() { }
    public Student(String firstName, String lastName, String documentNumber, String phoneNumber, LocalDate birthDate, String birthPlace, String address) {
        this.firstName = firstName; this.lastName = lastName; this.documentNumber = documentNumber; this.phoneNumber = phoneNumber; this.birthDate = birthDate; this.birthPlace = birthPlace; this.address = address;
    }
    public void update(String firstName, String lastName, String documentNumber, String phoneNumber, LocalDate birthDate, String birthPlace, String address) {
        this.firstName = firstName; this.lastName = lastName; this.documentNumber = documentNumber; this.phoneNumber = phoneNumber; this.birthDate = birthDate; this.birthPlace = birthPlace; this.address = address;
    }
    public void deactivate() { status = StudentStatus.INACTIVE; }
    public void activate() { status = StudentStatus.ACTIVE; }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDocumentNumber() { return documentNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBirthPlace() { return birthPlace; }
    public String getAddress() { return address; }
    public StudentStatus getStatus() { return status; }
}