package com.tournament.app.footycup.backend.dto.format.bracket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateBracketRequest(
        @NotNull @Min(2) Integer totalTeams
) {
}
