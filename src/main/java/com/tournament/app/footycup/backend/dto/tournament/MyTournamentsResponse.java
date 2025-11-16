package com.tournament.app.footycup.backend.dto.tournament;

import java.util.List;

public record MyTournamentsResponse(
        List<TournamentItemResponse> organized,
        List<TournamentItemResponse> refereeing,
        List<TournamentItemResponse> coaching,
        List<TournamentItemResponse> observing,
        List<TournamentItemResponse> allTournaments
) {
}
