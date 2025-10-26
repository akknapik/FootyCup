package com.tournament.app.footycup.backend.dto.tactics;

import java.time.Instant;
import java.util.List;

public record TacticsBoardStateRequest(
        List<TacticsLayerDto> layers,
        String activeLayerId,
        Instant lastUpdated
) {
}