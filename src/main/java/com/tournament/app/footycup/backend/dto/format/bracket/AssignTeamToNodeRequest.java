package com.tournament.app.footycup.backend.dto.format.bracket;

import jakarta.validation.constraints.NotNull;

public record AssignTeamToNodeRequest(
        @NotNull Long nodeId,
        @NotNull Long teamId,
        @NotNull Boolean homeTeam
) {
}
