package com.tournament.app.footycup.backend.dto.team;

public record TeamStatisticsResponse(
        Long teamId,
        String teamName,
        Integer matchesPlayed,
        Integer minutesPlayed,
        Integer goals,
        Integer yellowCards,
        Integer redCards,
        Integer substitutions,
        Integer otherEvents,
        Integer totalEvents
) {
}
