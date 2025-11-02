package com.tournament.app.footycup.backend.dto.tournament;

import com.tournament.app.footycup.backend.dto.common.UserRef;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record TournamentResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String status,
        String system,
        UserRef organizer,
        boolean publicVisible,
        boolean followed,
        boolean qrCodeGenerated,
        List<UserRef> referees,
        Map<String, Integer> scoringRules
) {
}
