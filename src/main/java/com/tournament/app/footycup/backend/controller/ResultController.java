package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.FormatService;
import com.tournament.app.footycup.backend.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament/{tournamentId}/results")
@RequiredArgsConstructor
public class ResultController {

    private final MatchService matchService;
    private final FormatService formatService;

    @PutMapping("/{matchId}")
    public ResponseEntity<Void> updateResults(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestBody Match updated,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        matchService.updateSingleMatchResult(tournamentId, matchId, updated, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getGroups(
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(formatService.getGroupsWithStandings(tournamentId, user));
    }

    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNode>> getBracket(
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(formatService.getBracketNodes(tournamentId, user));
    }

}
