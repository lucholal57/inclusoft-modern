package ar.org.inclusoft.api.staff;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity @Table(name = "staff_members")
public class StaffMember {
    @Id @UuidGenerator private UUID id;
    @Column(name = "full_name", nullable = false, length = 120) private String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private StaffProfile profile;
    @Column(name = "document_number", length = 20) private String documentNumber;
    @Column(name = "phone_number", length = 30) private String phoneNumber;
    @Column(length = 120) private String email;
    @Column(length = 120) private String profession;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    protected StaffMember() { }
    public StaffMember(String fullName, StaffProfile profile, String documentNumber, String phoneNumber, String email, String profession) { update(fullName, profile, documentNumber, phoneNumber, email, profession); }
    public void update(String fullName, StaffProfile profile, String documentNumber, String phoneNumber, String email, String profession) { this.fullName = fullName; this.profile = profile; this.documentNumber = documentNumber; this.phoneNumber = phoneNumber; this.email = email; this.profession = profession; }
    public void deactivate() { active = false; } public void activate() { active = true; }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public UUID getId() { return id; } public String getFullName() { return fullName; } public StaffProfile getProfile() { return profile; } public String getDocumentNumber() { return documentNumber; } public String getPhoneNumber() { return phoneNumber; } public String getEmail() { return email; } public String getProfession() { return profession; } public boolean isActive() { return active; }
}