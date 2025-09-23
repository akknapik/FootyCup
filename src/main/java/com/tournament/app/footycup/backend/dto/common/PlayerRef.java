package com.tournament.app.footycup.backend.dto.common;

import java.time.LocalDate;

public record PlayerRef(
        Long id,
        int number,
        String name,
        LocalDate birthDate
) {
}
