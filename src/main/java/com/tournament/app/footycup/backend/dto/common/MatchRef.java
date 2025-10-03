package com.tournament.app.footycup.backend.dto.common;

public record MatchRef(
        Long id,
        String name,
        Long teamHomeId,
        Long teamAwayId
) {
}
