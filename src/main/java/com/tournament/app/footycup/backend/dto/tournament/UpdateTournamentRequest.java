package com.tournament.app.footycup.backend.dto.tournament;

import java.time.LocalDate;

public record UpdateTournamentRequest(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        Boolean publicVisible
) {
}
