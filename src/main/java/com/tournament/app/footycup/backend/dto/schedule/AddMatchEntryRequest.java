package com.tournament.app.footycup.backend.dto.schedule;

import jakarta.validation.constraints.NotNull;

public record AddMatchEntryRequest(@NotNull Long matchId) {}

