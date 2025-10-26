package com.tournament.app.footycup.backend.dto.common;

public record MatchEventRef(
        Long id,
        TeamRef team,
        PlayerRef player,
        PlayerRef secondaryPlayer,
        String eventType,
        Integer minute,
        String description
) {
    public MatchEventRef(Long id, TeamRef team, PlayerRef player, String eventType, Integer minute) {
        this(id, team, player, null, eventType, minute, null);
    }

    public MatchEventRef(Long id, TeamRef team, PlayerRef player, PlayerRef secondaryPlayer, String eventType, Integer minute) {
        this(id, team, player, secondaryPlayer, eventType, minute, null);
    }
}
