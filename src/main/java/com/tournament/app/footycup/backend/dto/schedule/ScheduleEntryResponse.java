package com.tournament.app.footycup.backend.dto.schedule;

import com.tournament.app.footycup.backend.dto.common.MatchRef;

import java.time.LocalDateTime;

public record ScheduleEntryResponse(
        Long id,
        String type,
        LocalDateTime startDateTime,
        Integer durationInMin,
        MatchRef match) {
}
