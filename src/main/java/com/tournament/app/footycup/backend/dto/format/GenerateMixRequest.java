package com.tournament.app.footycup.backend.dto.format;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateMixRequest(
        @NotNull @Min(1) Integer groupCount,
        @NotNull @Min(2) Integer teamsPerGroup,
        @NotNull @Min(2) Integer advancing
) {
}
