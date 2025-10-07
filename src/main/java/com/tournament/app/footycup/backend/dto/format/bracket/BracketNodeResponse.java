package com.tournament.app.footycup.backend.dto.format.bracket;

import com.tournament.app.footycup.backend.dto.common.MatchRef;

public record BracketNodeResponse(
        Long id,
        Integer round,
        Integer position,
        Long parentHomeNodeId,
        Long parentAwayNodeId,
        MatchRef match
) {
}
