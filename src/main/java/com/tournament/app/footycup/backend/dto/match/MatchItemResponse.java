package com.tournament.app.footycup.backend.dto.match;

import com.tournament.app.footycup.backend.dto.common.TeamRef;
import com.tournament.app.footycup.backend.dto.common.UserRef;

public record MatchItemResponse(
    Long id,
    String name,
    String status,
    TeamRef teamHome,
    TeamRef teamAway,
    UserRef referee
    ) {
}
