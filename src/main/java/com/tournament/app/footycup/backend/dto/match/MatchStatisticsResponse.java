package com.tournament.app.footycup.backend.dto.match;

public record MatchStatisticsResponse(
        TeamMatchStatistics homeTeam,
        TeamMatchStatistics awayTeam
) {
}