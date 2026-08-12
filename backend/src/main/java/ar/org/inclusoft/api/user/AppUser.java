package ar.org.inclusoft.api.user;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;
import ar.org.inclusoft.api.staff.StaffMember;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @UuidGenerator private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String username;
    @Column(name = "display_name", nullable = false, length = 120) private String displayName;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private UserRole role;
    @Column(nullable = false) private boolean enabled = true;
    @Column(name = "must_change_password", nullable = false) private boolean mustChangePassword;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    @OneToOne @JoinColumn(name = "staff_member_id", unique = true) private StaffMember staffMember;
    protected AppUser() { }
    public AppUser(String username, String displayName, String passwordHash, UserRole role) { this(username, displayName, passwordHash, role, null); }
    public AppUser(String username, String displayName, String passwordHash, UserRole role, StaffMember staffMember) { this.username = username; this.displayName = displayName; this.passwordHash = passwordHash; this.role = role; this.staffMember = staffMember; }
    public void update(String displayName, UserRole role, StaffMember staffMember) { this.displayName = displayName; this.role = role; this.staffMember = staffMember; }
    public void deactivate() { enabled = false; }
    public void activate() { enabled = true; }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public StaffMember getStaffMember() { return staffMember; }
    public void requirePasswordChange() { mustChangePassword = true; }
    public void changePassword(String passwordHash) { this.passwordHash = passwordHash; this.mustChangePassword = false; }
}
