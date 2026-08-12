package ar.org.inclusoft.api.attendance;
import java.util.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*;
public record AttendanceRequest(@Size(max=1000) String notes,@NotEmpty List<@Valid Entry> entries){ public record Entry(@NotNull AttendanceParticipantType participantType,@NotNull UUID participantId,@NotNull AttendanceStatus status,@Size(max=500) String observation){} }
