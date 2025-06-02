package com.tournament.app.footycup.backend.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "Response containing authentication token expiration info")
public class AuthResponse {

    @Schema(
            description = "Time in seconds until the access token expires",
            example = "900"
    )
    private int expiresIn;
}