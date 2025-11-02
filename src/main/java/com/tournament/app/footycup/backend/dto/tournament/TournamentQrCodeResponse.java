package com.tournament.app.footycup.backend.dto.tournament;

public record TournamentQrCodeResponse(
        boolean generated,
        String imageBase64
) {
}