package com.tournament.app.footycup.backend.dto.schedule;

import java.time.LocalDateTime;

public record ScheduleListItemResponse(
        Long id,
        LocalDateTime startDateTime
) {
}
