package com.tournament.app.footycup.backend.dto.tactics;

import java.time.Instant;
import java.util.List;

public record TacticsLayerDto(
        String id,
        String name,
        List<TacticsTokenDto> tokens,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}