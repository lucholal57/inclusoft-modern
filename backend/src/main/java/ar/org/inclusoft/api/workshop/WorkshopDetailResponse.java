package ar.org.inclusoft.api.workshop;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import ar.org.inclusoft.api.staff.StaffMember;
import ar.org.inclusoft.api.student.Student;

public record WorkshopDetailResponse(UUID id, String name, String description, int capacity, WorkshopStatus status, List<TeamMember> teamMembers, List<PersonReference> students, List<Schedule> schedules) {
    static WorkshopDetailResponse from(Workshop workshop) {
        return new WorkshopDetailResponse(workshop.getId(), workshop.getName(), workshop.getDescription(), workshop.getCapacity(), workshop.getStatus(),
                workshop.getStaffMembers().stream().map(WorkshopDetailResponse::teamMember).toList(), workshop.getStudents().stream().map(WorkshopDetailResponse::student).toList(), workshop.getSchedules().stream().map(WorkshopDetailResponse::schedule).toList());
    }
    private static TeamMember teamMember(StaffMember member) { return new TeamMember(member.getId(), member.getFullName(), member.getProfile().name()); }
    private static PersonReference student(Student student) { return new PersonReference(student.getId(), student.getLastName() + ", " + student.getFirstName()); }
    private static Schedule schedule(WorkshopSchedule schedule) { return new Schedule(schedule.getId(), schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime(), schedule.getLocation()); }
    public record TeamMember(UUID id, String name, String profile) { }
    public record PersonReference(UUID id, String name) { }
    public record Schedule(UUID id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String location) { }
}
