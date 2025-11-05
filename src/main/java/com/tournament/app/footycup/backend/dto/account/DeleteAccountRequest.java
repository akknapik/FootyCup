package com.tournament.app.footycup.backend.dto.account;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "Password confirmation is required") String password
) {
}
