package ar.org.inclusoft.api.workshop;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record WorkshopResponse(UUID id, String name, String description, int capacity, WorkshopStatus status, int teamMemberCount, int studentCount, List<Schedule> schedules) {
    static WorkshopResponse from(Workshop workshop) {
        return new WorkshopResponse(workshop.getId(), workshop.getName(), workshop.getDescription(), workshop.getCapacity(), workshop.getStatus(), workshop.getStaffMembers().size(), workshop.getStudents().size(), workshop.getSchedules().stream().map(WorkshopResponse::schedule).toList());
    }
    private static Schedule schedule(WorkshopSchedule schedule) { return new Schedule(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime(), schedule.getLocation()); }
    public record Schedule(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String location) { }
}
