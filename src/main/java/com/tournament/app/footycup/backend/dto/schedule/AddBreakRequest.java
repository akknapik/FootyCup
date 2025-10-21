package com.tournament.app.footycup.backend.dto.schedule;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddBreakRequest(
        @NotNull @Min(1) Integer durationInMin
) {
}
