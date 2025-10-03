package com.tournament.app.footycup.backend.dto.common;

public record MatchEventRef(
        Long id,
        TeamRef team,
        PlayerRef player,
        String eventType,
        Integer minute
) {
}
