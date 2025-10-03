package com.tournament.app.footycup.backend.dto.match;

import jakarta.validation.constraints.NotNull;

public record AssignRefereeRequest(
        @NotNull Long matchId,
        @NotNull Long refereeId
) {
}
