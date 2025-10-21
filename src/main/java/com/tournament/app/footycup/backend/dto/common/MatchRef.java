package com.tournament.app.footycup.backend.dto.common;

public record MatchRef(
        Long id,
        String name,
        TeamRef teamHome,
        TeamRef teamAway,
        Integer homeScore,
        Integer awayScore) {
}
