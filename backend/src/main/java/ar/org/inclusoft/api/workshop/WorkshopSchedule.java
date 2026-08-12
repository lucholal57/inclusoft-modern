package ar.org.inclusoft.api.workshop;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "workshop_schedules")
public class WorkshopSchedule {
    @Id @UuidGenerator private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "workshop_id", nullable = false) private Workshop workshop;
    @Enumerated(EnumType.STRING) @Column(name = "day_of_week", nullable = false, length = 12) private DayOfWeek dayOfWeek;
    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "end_time", nullable = false) private LocalTime endTime;
    @Column(length = 120) private String location;
    protected WorkshopSchedule() { }
    WorkshopSchedule(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String location) { this.dayOfWeek = dayOfWeek; this.startTime = startTime; this.endTime = endTime; this.location = location; }
    void assignTo(Workshop assignedWorkshop) { this.workshop = assignedWorkshop; }
    public UUID getId() { return id; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
}
