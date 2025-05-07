package com.tournament.app.footycup.backend.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Request body for creating or updating a team")
public class TeamRequest {

    @Schema(description = "Team name", example = "FC Champions")
    private String name;

    @Schema(description = "Country the team represents", example = "Spain")
    private String country;

    @Schema(description = "Email of the team coach", example = "coach@example.com")
    private String coachEmail;
}
