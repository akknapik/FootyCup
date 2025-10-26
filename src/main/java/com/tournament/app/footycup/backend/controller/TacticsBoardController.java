package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardResponse;
import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardStateRequest;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.TacticsBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentId}/matches/{matchId}/tactics-board")
public class TacticsBoardController {

    private final TacticsBoardService tacticsBoardService;

    @GetMapping
    public ResponseEntity<TacticsBoardResponse> getBoard(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal User organizer) {
        var response = tacticsBoardService.getBoard(tournamentId, matchId, organizer);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<TacticsBoardResponse> saveBoard(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestBody TacticsBoardStateRequest state,
            @AuthenticationPrincipal User organizer) {
        var response = tacticsBoardService.saveBoard(tournamentId, matchId, organizer, state);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal User organizer) {
        tacticsBoardService.deleteBoard(tournamentId, matchId, organizer);
        return ResponseEntity.noContent().build();
    }
}