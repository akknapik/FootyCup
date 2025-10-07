package com.tournament.app.footycup.backend.dto.format.group;

import jakarta.validation.constraints.NotNull;

public record AssignTeamToSlotRequest(
        @NotNull Long slotId,
        @NotNull Long teamId
) {
}
