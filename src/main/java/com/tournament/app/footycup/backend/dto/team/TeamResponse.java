package com.tournament.app.footycup.backend.dto.team;

import com.tournament.app.footycup.backend.dto.common.PlayerRef;
import com.tournament.app.footycup.backend.dto.common.UserRef;

import java.util.List;

public record TeamResponse(
        Long id,
        String name,
        UserRef coach,
        String country,
        List<PlayerRef> players
) {
}
