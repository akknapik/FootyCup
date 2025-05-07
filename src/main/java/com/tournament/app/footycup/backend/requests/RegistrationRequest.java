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
@Schema(description = "Registration request to create a new user account")
public class RegistrationRequest {

    @Schema(description = "User's first name", example = "John")
    private final String firstname;

    @Schema(description = "User's last name", example = "Doe")
    private final String lastname;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private final String email;

    @Schema(description = "User's password", example = "SecurePassword123")
    private final String password;
}
