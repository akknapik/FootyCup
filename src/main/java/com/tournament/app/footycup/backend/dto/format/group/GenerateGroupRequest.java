package com.tournament.app.footycup.backend.dto.format.group;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateGroupRequest(
        @NotNull @Min(1) Integer groupCount,
        @NotNull @Min(2) Integer teamsPerGroup
) {
}
