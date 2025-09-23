package com.tournament.app.footycup.backend.dto.team;

import java.time.LocalDate;

public record UpdatePlayerRequest(
        String name,
        int number,
        LocalDate birthDate)
{}
