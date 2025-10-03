package com.tournament.app.footycup.backend.dto.match;

import jakarta.validation.constraints.NotNull;

public record CreateMatchEventRequest(
        Long playerId,
        @NotNull Long teamId,
        @NotNull String eventType,
        @NotNull Integer minute
) {
}
