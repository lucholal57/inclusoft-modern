package ar.org.inclusoft.api.attendance;
import java.time.*; import java.util.*;
public record AttendanceResponse(UUID workshopId,String workshopName,LocalDate date,boolean saved,String notes,List<Entry> entries){ public record Entry(AttendanceParticipantType participantType,UUID participantId,String participantName,AttendanceStatus status,String observation){} }
