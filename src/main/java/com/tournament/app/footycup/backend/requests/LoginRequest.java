package com.tournament.app.footycup.backend.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "Login request containing user credentials")
public class LoginRequest {

    @Schema(description = "Email of the user", example = "user@example.com")
    private final String email;

    @Schema(description = "User password", example = "Password123!")
    private final String password;
}
