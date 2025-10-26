package com.tournament.app.footycup.backend.dto.team;

public record PlayerStatisticsResponse(
        Long playerId,
        String playerName,
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
