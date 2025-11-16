package com.tournament.app.footycup.backend.dto.tournament;

import com.tournament.app.footycup.backend.dto.common.UserRef;

import java.time.LocalDate;

public record TournamentItemResponse(
        Long id,
        String code,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String status,
        UserRef organizer,
        boolean publicVisible,
        boolean followed
) {
}
