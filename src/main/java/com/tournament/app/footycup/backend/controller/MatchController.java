package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.match.MatchItemResponse;
import com.tournament.app.footycup.backend.dto.match.MatchResponse;
import com.tournament.app.footycup.backend.mapper.MatchMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/tournament/{tournamentId}/matches")
public class MatchController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @GetMapping
    public ResponseEntity<List<MatchItemResponse>> getMatches(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        var matches = matchService.getMatches(tournamentId, organizer);
        var dto = matches.stream().map(matchMapper::toItem).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal User organizer) {
        var match = matchService.getMatch(tournamentId, matchId, organizer);
        return ResponseEntity.ok(matchMapper.toResponse(match));
    }

    @PostMapping
    public ResponseEntity<Void> generateGroupMatches(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        matchService.generateGroupMatches(tournamentId, organizer);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        matchService.deleteAllMatches(tournamentId, organizer);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal User organizer) {
        matchService.deleteMatch(tournamentId, matchId, organizer);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{matchId}/referee")
    public ResponseEntity<MatchItemResponse> assignReferee(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestParam Long refereeId,
            @AuthenticationPrincipal User organizer) {
        var match = matchService.assignReferee(tournamentId, matchId, refereeId, organizer);
        return ResponseEntity.ok(matchMapper.toItem(match));
    }
}
