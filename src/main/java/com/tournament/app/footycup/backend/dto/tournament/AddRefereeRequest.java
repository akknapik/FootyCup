package com.tournament.app.footycup.backend.dto.tournament;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddRefereeRequest(
        @NotBlank @Email String email
) {
}
