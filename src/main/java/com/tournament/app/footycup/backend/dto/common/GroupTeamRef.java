package com.tournament.app.footycup.backend.dto.common;

public record GroupTeamRef(
        Long id,
        TeamRef team,
        Integer position,
        Integer points,
        Integer goalsFor,
        Integer goalsAgainst
) {
}