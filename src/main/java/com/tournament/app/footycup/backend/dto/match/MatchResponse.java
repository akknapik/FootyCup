package com.tournament.app.footycup.backend.dto.match;

import com.tournament.app.footycup.backend.dto.common.MatchEventRef;
import com.tournament.app.footycup.backend.dto.common.TeamRef;
import com.tournament.app.footycup.backend.dto.common.UserRef;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MatchResponse(
        Long id,
        String name,
        String status,
        LocalDate matchDate,
        LocalTime matchTime,
        Integer durationInMin,
        TeamRef teamHome,
        TeamRef teamAway,
        Integer homeScore,
        Integer awayScore,
        Long groupId,
        UserRef referee,
        List<MatchEventRef> events
) {}
