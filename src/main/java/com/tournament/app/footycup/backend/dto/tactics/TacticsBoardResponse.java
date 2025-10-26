package com.tournament.app.footycup.backend.dto.tactics;

import java.time.Instant;
import java.util.List;

public record TacticsBoardResponse(
        Long id,
        Long matchId,
        List<TacticsLayerDto> layers,
        String activeLayerId,
        Instant lastUpdated,
        Instant savedAt
) {
}
