package com.tournament.app.footycup.backend.dto.team;

import com.tournament.app.footycup.backend.dto.common.UserRef;

public record TeamItemResponse(
        Long id,
        String name,
        UserRef coach,
        int playersCount
) {
}
