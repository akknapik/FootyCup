package com.tournament.app.footycup.backend.dto.tactics;

public record TacticsTokenDto(
        String id,
        String type,
        String label,
        String description,
        double x,
        double y,
        String color,
        Long referenceId
) {
}