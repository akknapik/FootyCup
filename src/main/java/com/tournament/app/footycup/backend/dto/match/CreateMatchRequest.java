package com.tournament.app.footycup.backend.dto.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMatchRequest(
        @NotBlank String name,
        @NotNull Long teamHomeId,
        @NotNull Long teamAwayId
) {
}
