package com.tournament.app.footycup.backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePlayerRequest(
        @NotBlank String name,
        @NotNull int number,
        @NotNull LocalDate birthDate
) {
}
