package com.tournament.app.footycup.backend.controller;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.TournamentService;
import com.tournament.app.footycup.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    private final UserService userService;

    public TournamentController(TournamentService tournamentService, UserService userService) {
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<Tournament>> getMyTournaments(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        try {
            List<Tournament> myTournaments = tournamentService.getTournamentsByOrganizer(user);
            return ResponseEntity.ok(myTournaments);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament request, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User organizer = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.createTournament(request, organizer);
        return ResponseEntity.ok(tournament);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.getTournamentById(id, user);
        return ResponseEntity.ok(tournament);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id,
                                             @RequestBody Tournament updatedData,
                                             Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        Tournament updated = tournamentService.updateTournament(id, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        tournamentService.deleteTournament(id, user);
        return ResponseEntity.noContent().build();
    }
}
