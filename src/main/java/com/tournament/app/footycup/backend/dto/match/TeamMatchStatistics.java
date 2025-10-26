package com.tournament.app.footycup.backend.dto.match;

public record TeamMatchStatistics(
        Long teamId,
        String teamName,
        long goals,
        long yellowCards,
        long redCards,
        long substitutions,
        long otherEvents,
        long totalEvents
) {
}