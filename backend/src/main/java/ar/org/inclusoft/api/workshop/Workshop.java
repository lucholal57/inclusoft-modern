package ar.org.inclusoft.api.workshop;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import ar.org.inclusoft.api.staff.StaffMember;
import ar.org.inclusoft.api.student.Student;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "workshops")
public class Workshop {
    @Id @UuidGenerator private UUID id;
    @Column(nullable = false, unique = true, length = 120) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false) private int capacity;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private WorkshopStatus status = WorkshopStatus.ACTIVE;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    @ManyToMany @JoinTable(name = "workshop_staff_members", joinColumns = @JoinColumn(name = "workshop_id"), inverseJoinColumns = @JoinColumn(name = "staff_member_id")) private Set<StaffMember> staffMembers = new LinkedHashSet<>();
    @ManyToMany @JoinTable(name = "workshop_students", joinColumns = @JoinColumn(name = "workshop_id"), inverseJoinColumns = @JoinColumn(name = "student_id")) private Set<Student> students = new LinkedHashSet<>();
    @OneToMany(mappedBy = "workshop", cascade = CascadeType.ALL, orphanRemoval = true) private Set<WorkshopSchedule> schedules = new LinkedHashSet<>();

    protected Workshop() { }
    public Workshop(String name, String description, int capacity) { this.name = name; this.description = description; this.capacity = capacity; }
    public void update(String name, String description, int capacity) { this.name = name; this.description = description; this.capacity = capacity; }
    public void deactivate() { status = WorkshopStatus.INACTIVE; }
    public void activate() { status = WorkshopStatus.ACTIVE; }
    public void assignStaffMembers(Set<StaffMember> selectedStaffMembers) { staffMembers.clear(); staffMembers.addAll(selectedStaffMembers); }
    public void enrollStudents(Set<Student> selectedStudents) { students.clear(); students.addAll(selectedStudents); }
    public void defineSchedules(Set<WorkshopSchedule> selectedSchedules) { schedules.clear(); selectedSchedules.forEach(schedule -> { schedule.assignTo(this); schedules.add(schedule); }); }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCapacity() { return capacity; }
    public WorkshopStatus getStatus() { return status; }
    public Set<StaffMember> getStaffMembers() { return staffMembers; }
    public Set<Student> getStudents() { return students; }
    public Set<WorkshopSchedule> getSchedules() { return schedules; }
}
