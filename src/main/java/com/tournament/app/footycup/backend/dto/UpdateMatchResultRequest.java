package com.tournament.app.footycup.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateMatchResultRequest(
        @NotNull Long matchId,
        @NotNull @Min(0) Integer homeScore,
        @NotNull @Min(0) Integer awayScore
) {
}
