package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping("/tournament/{tournamentId}/matches")
public class MatchController {
    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<Match>> getMatches(@PathVariable Long tournamentId,
                                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Match> matches = matchService.getMatches(tournamentId, user);
        return ResponseEntity.ok(matches);
    }

    @PostMapping
    public ResponseEntity<Void> generateGroupMatches(@PathVariable Long tournamentId,
                                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.generateGroupMatches(tournamentId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches(@PathVariable Long tournamentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.deleteAllMatches(tournamentId, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long tournamentId, @PathVariable Long matchId,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.deleteMatch(tournamentId, matchId, user);
        return ResponseEntity.noContent().build();
    }
}
