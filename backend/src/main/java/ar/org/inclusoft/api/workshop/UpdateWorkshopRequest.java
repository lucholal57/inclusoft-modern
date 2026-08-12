package ar.org.inclusoft.api.workshop;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWorkshopRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 500) String description,
        @Min(1) @Max(500) int capacity,
        List<@Valid WorkshopScheduleRequest> schedules
) { }
