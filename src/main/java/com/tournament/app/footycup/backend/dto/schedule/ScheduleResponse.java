package com.tournament.app.footycup.backend.dto.schedule;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleResponse(
        Long id,
        LocalDateTime startDateTime,
        List<ScheduleEntryResponse> entries
) {
}
