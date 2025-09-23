package com.tournament.app.footycup.backend.dto.team;

import jakarta.validation.constraints.Email;

public record UpdateTeamRequest(
        String name,
        String country,
        @Email String coachEmail
) {
}
