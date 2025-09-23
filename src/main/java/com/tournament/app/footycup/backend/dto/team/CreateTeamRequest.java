package com.tournament.app.footycup.backend.dto.team;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank String name,
        String country,
        @NotBlank @Email String coachEmail
) {
}
