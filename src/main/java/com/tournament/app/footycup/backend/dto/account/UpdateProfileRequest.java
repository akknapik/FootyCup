package com.tournament.app.footycup.backend.dto.account;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank(message = "Firstname is required") String firstname,
        @NotBlank(message = "Lastname is required") String lastname
) {
}
